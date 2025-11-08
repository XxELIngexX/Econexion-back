package io.econexion.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest request = mock(WebRequest.class);

    @Test
    void handleIllegalState_returnsConflict() {
        when(request.getDescription(false)).thenReturn("uri=/api/test");

        ResponseEntity<Object> res =
                handler.handleIllegalState(new IllegalStateException("duplicado"), request);

        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertTrue(res.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) res.getBody();
        assertEquals("Conflicto lógico", body.get("error"));
    }

    @Test
    void handleDataIntegrityViolation_detectsConstraint() {
        when(request.getDescription(false)).thenReturn("uri=/api/users");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key",
                new SQLException("duplicate entry 'email'"));

        ResponseEntity<Object> res = handler.handleDataIntegrityViolation(ex, request);
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) res.getBody();
        assertEquals("Conflicto de datos", body.get("error"));
        assertTrue(body.get("message").toString().contains("duplicate"));
    }

    @Test
    void handleDataIntegrityViolation_fallsBackToGeneric() {
        when(request.getDescription(false)).thenReturn("uri=/api/users");
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("non unique reason");

        ResponseEntity<Object> res = handler.handleDataIntegrityViolation(ex, request);

        // El handler devuelve 409 (CONFLICT) incluso en casos genéricos
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertTrue(res.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) res.getBody();
        assertEquals("Conflicto de datos", body.get("error"));
    }

    @Test
    void handleGenericException_returns500() {
        when(request.getDescription(false)).thenReturn("uri=/api/error");
        Exception ex = new RuntimeException("boom");

        ResponseEntity<Object> res = handler.handleGenericException(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) res.getBody();
        assertEquals("Error interno del servidor", body.get("error"));
        assertTrue(body.get("message").toString().contains("boom"));
    }
}
