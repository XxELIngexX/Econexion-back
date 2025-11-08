package io.econexion.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthIntegrationFlowTest {

    private JwtUtil jwt;
    private ObjectMapper om;
    private AuthenticationManager am;
    private SecurityConfig.LoginController controller;
    private SecurityConfig.SimpleJwtFilter filter;

    @BeforeEach
    void setup() {
        String secret = "01234567890123456789012345678901-very-strong-secret-key-256-bits";
        jwt = new JwtUtil(secret, 5);
        om = new ObjectMapper();
        am = mock(AuthenticationManager.class);
        controller = new SecurityConfig.LoginController(am, jwt, om);
        filter = new SecurityConfig.SimpleJwtFilter(jwt, am);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("🔄 Flujo completo: login → token → filtro JWT")
    void fullAuthFlow_validToken_passesFilter() throws Exception {
        // === 1️⃣ LOGIN: generar token ===
        var reqLogin = mock(jakarta.servlet.http.HttpServletRequest.class);
        var resLogin = mock(HttpServletResponse.class);
        var out = new ByteArrayOutputStream();
        String json = "{\"email\":\"full@test.com\",\"password\":\"1234\"}";

        when(reqLogin.getInputStream()).thenReturn(new jakarta.servlet.ServletInputStream() {
            private final ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes());
            @Override public int read() { return bais.read(); }
            @Override public boolean isFinished() { return false; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        });

        when(resLogin.getOutputStream()).thenReturn(new jakarta.servlet.ServletOutputStream() {
            @Override public void write(int b) throws IOException { out.write(b); }
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
        });

        // Mock autenticación exitosa
        Authentication auth = new UsernamePasswordAuthenticationToken("full@test.com", "1234");
        when(am.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        // Ejecutar login
        controller.login(reqLogin, resLogin);
        String body = out.toString();
        assertTrue(body.contains("token"));

        String token = om.readTree(body).get("token").asText();
        assertEquals("full@test.com", jwt.extractUserName(token));

        // === 2️⃣ FILTRO: validar token en request ===
        var reqProtected = mock(HttpServletRequest.class);
        var resProtected = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(reqProtected.getHeader("Authorization")).thenReturn("Bearer " + token);
        filter.doFilterInternal(reqProtected, resProtected, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("full@test.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(reqProtected, resProtected);
    }

    @Test
    @DisplayName("❌ Token inválido → respuesta 401 y detiene cadena")
    void invalidToken_rejectedByFilter() throws Exception {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Bearer BADTOKEN");
        doNothing().when(res).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());

        filter.doFilterInternal(req, res, chain);

        verify(res).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), contains("Invalid token"));
        verify(chain, never()).doFilter(req, res);
    }
}
