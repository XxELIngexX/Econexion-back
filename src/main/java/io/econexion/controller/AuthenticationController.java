package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para login y registro de usuarios")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtUtil jwtUtil,
                                    UserService userService,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ==== DTOs ====
    public record LoginRequest(@NotBlank String email, @NotBlank String password) { }
    public record LoginResponse(String token) { }
    public record RegisterRequest(@NotBlank String username,
                                  @NotBlank String password,
                                  @NotBlank String email,
                                  @NotBlank String enterpriseName,
                                  @NotBlank String nit,
                                  @NotBlank String rol) { }

    // ==== LOGIN ====
    @Operation(summary = "Autenticar usuario", description = "Recibe email y contraseña y devuelve un JWT")
    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        log.info("🔐 Intentando autenticar usuario: {}", req.email());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );
            String token = jwtUtil.generate(req.email());
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
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        log.info("🧾 Solicitud de registro recibida para {}", user.getEmail());
        try {
            Optional<User> existing = userService.findByEmail(user.getEmail());
            if (existing.isPresent()) {
                log.warn("⚠️ Intento de registro con email existente: {}", user.getEmail());
                return ResponseEntity.badRequest().body("Email is already in use");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User newUser = userService.create(user);
            log.info("✅ Usuario registrado exitosamente: {}", newUser.getEmail());
            return ResponseEntity.ok(newUser);

        } catch (Exception e) {
            log.error("❌ Error al registrar usuario {}: {}", user.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }
}
