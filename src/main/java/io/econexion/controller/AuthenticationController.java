package io.econexion.controller;

import io.econexion.service.UserService;
import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para login y registro de usuarios")


public class AuthenticationController {
    private UserService userService;
    private  AuthenticationManager authenticationManager;
    private  JwtUtil jwtUtil;
    private  PasswordEncoder passwordEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
 

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    // ==== DTOs ====
    public record LoginRequest(@NotBlank String email,
                               @NotBlank String password) {
    }

    public record LoginResponse(String token) {
    }

    public record RegisterRequest(@NotBlank String username,
                                  @NotBlank String password,
                                  @NotBlank String email,
                                  @NotBlank String enterpriseName,
                                  @NotBlank String nit,
                                  @NotBlank String rol
    ) {
    }


    @Operation(summary = "Autenticar usuario", description = "Recibe email y contraseña y devuelve un JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );
            String token = jwtUtil.generate(req.email());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña inválidos");
        }
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario y devuelve su información")

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) throws Exception {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }
        User newUser = userService.create(
                user
        );

        return ResponseEntity.ok(newUser);
    }
}
