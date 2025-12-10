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

/**
 * Spring Security configuration responsible for defining:
 * <ul>
 *     <li>Password encoding strategy (BCrypt)</li>
 *     <li>User lookup for authentication</li>
 *     <li>JWT utility initialization</li>
 *     <li>AuthenticationManager using DAO provider</li>
 *     <li>SecurityFilterChain rules (public vs. secured endpoints)</li>
 *     <li>Custom JWT filter for validating incoming tokens</li>
 * </ul>
 * 
 * This configuration ensures that API endpoints are secured when needed,
 * tokens are validated for protected routes, and authentication is handled properly.
 */
@Configuration
public class SecurityConfig {

    /**
     * Defines BCrypt as the password encoder, used by Spring Security
     * to verify hashed passwords during authentication.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Loads users from the database using their email (used as username).
     * <p>
     * Builds Spring Security UserDetails for authentication.
     *
     * @param repo repository that queries User entities
     * @return UserDetailsService for authentication
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return email -> repo.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    /**
     * Initializes JwtUtil using values from application properties.
     *
     * @param secret JWT signing secret
     * @param expirationMinutes token lifetime in minutes
     * @return configured JwtUtil instance
     */
    @Bean
    public JwtUtil jwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        return new JwtUtil(secret, expirationMinutes);
    }

    /**
     * Configures AuthenticationManager using a DAO-based provider
     * and the defined password encoder.
     *
     * @param uds UserDetailsService implementation
     * @param enc password encoder (BCrypt)
     * @return AuthenticationManager instance
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder enc) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(enc);
        return new ProviderManager(provider);
    }

    /**
     * Registers the custom JWT filter that intercepts requests
     * and validates authorization tokens.
     *
     * @param jwtUtil utility used for reading/verifying JWTs
     * @param am authentication manager
     * @return SimpleJwtFilter instance
     */
    @Bean
    public SimpleJwtFilter simpleJwtFilter(JwtUtil jwtUtil, AuthenticationManager am) {
        return new SimpleJwtFilter(jwtUtil, am);
    }

    /**
     * Defines security rules for the API, such as:
     * <ul>
     *     <li>Allowing login without authentication</li>
     *     <li>Allowing GET requests on certain endpoints</li>
     *     <li>Requiring authentication for others</li>
     * </ul>
     * Also enables H2 console frame rendering and registers the JWT filter.
     *
     * @param http HttpSecurity object to configure
     * @param jwtFilter custom JWT filter
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SimpleJwtFilter jwtFilter) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/weather/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/weather/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/").authenticated()
                .anyRequest().permitAll());

        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));

        http.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Custom JWT filter that intercepts incoming requests,
     * validates Bearer tokens, and sets the authentication context.
     */
    static class SimpleJwtFilter extends BasicAuthenticationFilter {
        private final JwtUtil jwtUtil;

        /**
         * Creates an instance of the JWT filter.
         *
         * @param jwtUtil utility for verifying JWT tokens
         * @param authenticationManager authentication manager to delegate to
         */
        public SimpleJwtFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
            super(authenticationManager);
            this.jwtUtil = jwtUtil;
        }

        /**
         * Extracts and validates a JWT from the Authorization header.
         * If valid, populates the SecurityContext with the authenticated user.
         *
         * @param req incoming request
         * @param res outgoing response
         * @param chain filter chain continuation
         * @throws IOException thrown on I/O error
         * @throws ServletException thrown on servlet error
         */
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

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception e) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }
            }
            chain.doFilter(req, res);
        }
    }

    /** Compact DTO for login request payload. */
    public record LoginRequest(String email, String password) {}

    /** Compact DTO for login response payload (JWT token). */
    public record LoginResponse(String token) {}

    /**
     * Internal controller used for JWT-based login.
     * <p>
     * Reads JSON credentials from request body, authenticates the user,
     * and returns a JWT token on success.
     * </p>
     */
    public static class LoginController {
        private final AuthenticationManager am;
        private fin
