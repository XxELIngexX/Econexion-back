package io.econexion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean de PasswordEncoder para encriptar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de Spring Security.
     * Permite acceso sin autenticación a endpoints específicos.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (común en APIs REST)
            .csrf(csrf -> csrf.disable())
            
            // Configurar qué endpoints requieren autenticación
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                // .requestMatchers(
                //     "/lab/users/oauth2",      // ← Endpoint para Auth Service
                //     "/lab/users/by-email/**", // ← Por si lo usas después
                //     "/lab/users/getUser/**",  // ← Por si lo usas después
                //     "/actuator/**"            // ← Health checks
                // ).permitAll()
                
                // Todos los demás requieren autenticación
                // .anyRequest().authenticated()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}