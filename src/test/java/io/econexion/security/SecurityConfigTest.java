package io.econexion.security;

import io.econexion.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "jwt.secret=01234567890123456789012345678901-very-strong-secret-key-256-bits",
        "jwt.expiration-minutes=5"
})
class SecurityConfigTest {

    @Autowired
    private ApplicationContext ctx;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("🔐 PasswordEncoder bean usa BCrypt")
    void passwordEncoder_isBCrypt() {
        PasswordEncoder enc = ctx.getBean(PasswordEncoder.class);
        String encoded = enc.encode("password123");
        assertTrue(encoded.startsWith("$2"), "Debe usar BCrypt");
    }

    @Test
    @DisplayName("⚙️ AuthenticationManager se crea correctamente")
    void authenticationManager_exists() {
        AuthenticationManager am = ctx.getBean(AuthenticationManager.class);
        assertNotNull(am, "Debe existir AuthenticationManager");
    }

    @Test
    @DisplayName("🪶 JwtUtil genera y valida tokens correctamente")
    void jwtUtil_isConfigured() {
        JwtUtil jwt = ctx.getBean(JwtUtil.class);
        assertNotNull(jwt);
        String token = jwt.generate("tester@example.com");
        assertEquals("tester@example.com", jwt.extractUserName(token));
    }

    @Test
    @DisplayName("🧱 SecurityFilterChain se construye manualmente con mock de HttpSecurity")
    void securityFilterChain_builds() throws Exception {
        SecurityConfig config = ctx.getBean(SecurityConfig.class);
        HttpSecurity mockHttp = Mockito.mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);

        SecurityFilterChain chain = config.securityFilterChain(
                mockHttp,
                config.simpleJwtFilter(
                        ctx.getBean(JwtUtil.class),
                        ctx.getBean(AuthenticationManager.class)
                )
        );

        assertNotNull(chain, "Debe retornar instancia válida de SecurityFilterChain");
    }
}
