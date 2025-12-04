package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import io.econexion.service.GoogleTokenVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;


import org.springframework.security.authentication.AuthenticationManager;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final GoogleTokenVerifier googleVerifier;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UserService userService,
            JwtUtil jwtUtil,
            GoogleTokenVerifier googleVerifier,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.googleVerifier = googleVerifier;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
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

    // ==== DTOs ====
    public record LoginRequest(String email,
            String password) {
    }

    public record LoginResponse(String token) {
    }

    public record RegisterRequest(@NotBlank String username,
            @NotBlank String password,
            @NotBlank String email,
            @NotBlank String enterpriseName,
            @NotBlank String nit,
            @NotBlank String rol) {
    }

    // ==== LOGIN ====
    @Operation(summary = "Autenticar usuario", description = "Recibe email y contraseña y devuelve un JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        log.info("🔐 Intentando autenticar usuario: {}", req.email());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password()));

            Optional<User> optionalUser = userService.findByEmail(req.email());
            if (optionalUser.isEmpty()) {
                log.warn("⚠️ Usuario autenticado pero no encontrado en DB: {}", req.email());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error interno: Usuario no encontrado post-autenticación");
            }
            User user = optionalUser.get();

            String token = jwtUtil.generate(user.getUsername()); // Modificado: Pasa User completo
            log.info("✅ Autenticación exitosa para {}", req.email());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException ex) {
            log.warn("❌ Fallo de autenticación para {}: {}", req.email(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña inválidos");
        } catch (Exception e) {
            log.error("⚠️ Error inesperado en login: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error interno al autenticar usuario");
        }
    }

    // ==== REGISTER ====
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario y devuelve su información")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) { // Cambiado a RegisterRequest
        log.info("🧾 Solicitud de registro recibida para {}", request.email());
        try {
            Optional<User> existing = userService.findByEmail(request.email());
            if (existing.isPresent()) {
                log.warn("⚠️ Intento de registro con email existente: {}", request.email());
                return ResponseEntity.badRequest().body("Email is already in use");
            }

            // Nuevo: Mapear DTO a User
            User user = new User();
            user.setId(UUID.randomUUID()); // Asumiendo ID generado aquí
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setEnterpriseName(request.enterpriseName());
            user.setNit(request.nit());
            user.setRol(request.rol().toUpperCase()); // Convierte String a Enum

            User newUser = userService.create(user);
            log.info("✅ Usuario registrado exitosamente: {}", newUser.getEmail());
            return ResponseEntity.ok(newUser);

        } catch (Exception e) {
            log.error("❌ Error al registrar usuario {}: {}", request.email(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

}
