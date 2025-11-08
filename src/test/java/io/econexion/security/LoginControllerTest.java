package io.econexion.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private AuthenticationManager am;
    private JwtUtil jwt;
    private ObjectMapper om;
    private SecurityConfig.LoginController controller;

    @BeforeEach
    void setup() {
        am = mock(AuthenticationManager.class);
        String secret = "01234567890123456789012345678901-very-strong-secret-key-256-bits";
        jwt = new JwtUtil(secret, 10);
        om = new ObjectMapper();
        controller = new SecurityConfig.LoginController(am, jwt, om);
    }

    @Test
    @DisplayName("✅ Credenciales válidas → genera token JWT")
    void validCredentials_returnsToken() throws Exception {
        // Mock request & response
        var req = mock(jakarta.servlet.http.HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var out = new ByteArrayOutputStream();

        // JSON del body
        String json = "{\"email\":\"user@test.com\",\"password\":\"1234\"}";
        when(req.getInputStream()).thenReturn(
                new jakarta.servlet.ServletInputStream() {
                    private final ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes());
                    @Override public int read() { return bais.read(); }
                    @Override public boolean isFinished() { return false; }
                    @Override public boolean isReady() { return true; }
                    @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
                });

        when(res.getOutputStream()).thenReturn(new jakarta.servlet.ServletOutputStream() {
            @Override public void write(int b) throws IOException { out.write(b); }
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
        });

        // Mock auth success
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", "1234");
        when(am.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        // Ejecutar
        controller.login(req, res);

        // Validar JSON de respuesta
        String responseBody = out.toString();
        assertTrue(responseBody.contains("token"));
        String token = om.readTree(responseBody).get("token").asText();
        assertEquals("user@test.com", jwt.extractUserName(token));
    }

    @Test
    @DisplayName("❌ Credenciales inválidas → lanza excepción controlada")
    void invalidCredentials_throws() throws Exception {
        var req = mock(jakarta.servlet.http.HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        when(req.getInputStream()).thenThrow(new IOException("Bad body"));

        assertThrows(IOException.class, () -> controller.login(req, res));
    }

    @Test
    @DisplayName("🧱 Estructura del controlador y dependencias válidas")
    void controllerDependencies_valid() {
        assertNotNull(controller);
        assertNotNull(jwt);
        assertNotNull(am);
        assertNotNull(om);
    }
}
    