package io.econexion.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleJwtFilterTest {

    private JwtUtil jwtUtil;
    private SecurityConfig.SimpleJwtFilter filter;
    private AuthenticationManager authManager;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        String secret = "01234567890123456789012345678901-very-strong-secret-key-256-bits";
        jwtUtil = new JwtUtil(secret, 5);
        authManager = mock(AuthenticationManager.class);
        filter = new SecurityConfig.SimpleJwtFilter(jwtUtil, authManager);
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("✅ Token válido → establece autenticación en contexto")
    void validToken_setsAuthentication() throws Exception {
        String token = jwtUtil.generate("user@test.com");
        when(req.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@test.com",
                SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(req, res);
    }

    @Test
    @DisplayName("❌ Token inválido → responde 401 y no continúa el chain")
    void invalidToken_returns401() throws Exception {
        when(req.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        doNothing().when(res).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());

        filter.doFilterInternal(req, res, chain);

        verify(res).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), contains("Invalid token"));
        verify(chain, never()).doFilter(req, res);
    }

    @Test
    @DisplayName("🕳 Sin header Authorization → no modifica contexto")
    void noHeader_doesNothing() throws Exception {
        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    @Test
    @DisplayName("🔄 Token válido → mantiene flujo hasta chain.doFilter")
    void validToken_callsChain() throws Exception {
        String token = jwtUtil.generate("another@user.com");
        when(req.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        assertEquals("another@user.com",
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
