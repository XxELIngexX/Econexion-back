package io.econexion.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.econexion.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.List;

// <- se activa con el perfil 'lab'
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
                        .withUsername(user.getEmail()) // aquí usas el email
                        .password(user.getPassword()) // ya está en BCrypt
                        // .roles(user.getRole() != null ? user.getRole() : "USER")
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
    public SimpleJwtFilter simpleJwtFilter(JwtUtil jwtUtil, AuthenticationManager am) {
        return new SimpleJwtFilter(jwtUtil, am);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SimpleJwtFilter jwtFilter) throws Exception {
        // Para demo/lab
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/weather/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/weather/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/").authenticated()
                .anyRequest().permitAll());

        // Necesario para que H2 se renderice en un frame
        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));

        http.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);
        return http.build();
    }

    // @Bean
    // public LoginController loginController(AuthenticationManager am, JwtUtil jwt,
    // ObjectMapper om) {
    // return new LoginController(am, jwt, om);
    // }

    // ===== soporte =====
    static class SimpleJwtFilter extends BasicAuthenticationFilter {
        private final JwtUtil jwtUtil;

        public SimpleJwtFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
            super(authenticationManager);
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                throws IOException, ServletException {

            String authHeader = req.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String username = Jwts.parserBuilder()
                            .setSigningKey(jwtUtil.key())
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
                            .getSubject();

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, List.of());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception e) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }
            }
            chain.doFilter(req, res);
        }
    }

    public record LoginRequest(String email, String password) {
    }

    public record LoginResponse(String token) {
    }

    public static class LoginController {
        private final AuthenticationManager am;
        private final JwtUtil jwt;
        private final ObjectMapper om;

        public LoginController(AuthenticationManager am, JwtUtil jwt, ObjectMapper om) {
            this.am = am;
            this.jwt = jwt;
            this.om = om;
        }

        @org.springframework.web.bind.annotation.PostMapping(value = "/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
            LoginRequest body = om.readValue(req.getInputStream(), LoginRequest.class);
            Authentication auth = am.authenticate(
                    new UsernamePasswordAuthenticationToken(body.email(), body.password()));
            String token = jwt.generate(auth.getName());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            om.writeValue(res.getOutputStream(), new LoginResponse(token));
        }
    }
}
