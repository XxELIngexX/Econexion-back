package io.econexion.security;

import io.econexion.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return email -> repo.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Bean
    public JwtUtil jwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        return new JwtUtil(secret, expirationMinutes);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder enc) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(enc);
        return new ProviderManager(provider);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
            AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(jwtUtil, authenticationManager);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register/google").permitAll()
                        .requestMatchers("/api/auth/login/google").permitAll()
                        //.requestMatchers("/lab/users/get/**").authenticated()
                        .anyRequest().permitAll());

        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));

        http.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ===== JWT AUTHENTICATION FILTER =====
    static class JwtAuthenticationFilter extends BasicAuthenticationFilter {
        private final JwtUtil jwtUtil;

        public JwtAuthenticationFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
            super(authenticationManager);
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            String path = request.getRequestURI();
            log.info("🔍 Request: {} {}", request.getMethod(), path);

            // No aplicar filtro a rutas públicas de autenticación
            if (path.startsWith("/api/auth/")) {
                log.info("🔓 Ruta pública permitida: {}", path);
                chain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String email = Jwts.parserBuilder()
                            .setSigningKey(jwtUtil.key())
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
                            .getSubject();

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
                            null, List.of());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception e) {
                    log.warn("❌ JWT inválido: {}", e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            }

            chain.doFilter(request, response);
        }
    }
}