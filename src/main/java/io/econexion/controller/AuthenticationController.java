package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.UserService;
import io.econexion.service.GoogleTokenVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final GoogleTokenVerifier googleVerifier;

    public AuthenticationController(UserService userService,
            JwtUtil jwtUtil,
            GoogleTokenVerifier googleVerifier) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.googleVerifier = googleVerifier;
    }

    // ========= DTOs =========
    public record GoogleRegisterRequest(
            String enterpriseName,
            String username,
            String nit,
            String email,
            String role) {
    }

    public record GoogleLoginRequest(
            String accessToken) {
    }

    public record AuthResponse(
            String jwt,
            String email,
            String username,
            String rol) {
    }

    // ========= REGISTER WITH GOOGLE =========
    @PostMapping("/register/google")
    public ResponseEntity<?> registerWithGoogle(@RequestHeader("Authorization") String authHeader,
            @RequestBody GoogleRegisterRequest req) {
        try {
            log.info("📥 Registro con Google recibido");
            log.info("Datos: enterpriseName={}, username={}, nit={}, email={}, role={}",
                    req.enterpriseName(), req.username(), req.nit(), req.email(), req.role());

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid Authorization header");
            }

            String accessToken = authHeader.substring(7);
            var googleUser = googleVerifier.verify(accessToken);
            if (googleUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Google token");
            }
            String email = googleUser.email();
            String name = googleUser.name();

            // 2. Verificar si ya existe
            Optional<User> existing = userService.findByEmail(email);
            if (existing.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User already registered");
            }

            // // 3. Crear usuario

            User newUser = new User();
            newUser.setEnterpriseName(req.enterpriseName());
            newUser.setUsername(req.username());
            newUser.setNit(req.nit());
            newUser.setEmail(email);
            newUser.setRol(req.role());
            newUser.setPassword(null);

            User saved = userService.create(newUser);

            log.info("🎉 Usuario creado: {}", saved.getEmail());
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            log.error("🔥 Error en registro Google: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // ========= LOGIN WITH GOOGLE =========
    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest req) {
        try {
            log.info("🔐 Login con Google recibido");

            if (req.accessToken() == null || req.accessToken().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("accessToken is required");
            }

            var googleUser = googleVerifier.verify(req.accessToken());
            if (googleUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Google token");
            }

            String email = googleUser.email();

            // 2. Buscar usuario
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not registered");
            }

            User user = userOpt.get();

            // 3. Generar tu JWT
            String jwt = jwtUtil.generate(email);

            log.info("✅ Login exitoso para {}", email);

            return ResponseEntity.ok(
                    new AuthResponse(jwt, email, user.getUsername(), user.getRol()));

        } catch (IllegalArgumentException e) {
            log.error("❌ Argumento inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            log.error("🔥 Error en login Google: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
