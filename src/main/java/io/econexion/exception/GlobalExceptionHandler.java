package io.econexion.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🌐 GlobalExceptionHandler — Manejador global de excepciones.
 * Intercepta y devuelve respuestas JSON limpias y consistentes con códigos HTTP adecuados.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** 🧩 Maneja errores de validación @Valid (campos vacíos, formatos incorrectos, etc.) */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validación fallida");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            fieldErrors.put(fieldName, message);
        });

        body.put("fields", fieldErrors);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /** ⚠️ Maneja errores de integridad de datos (duplicados o constraint violation) */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        Throwable root = getRootCause(ex);
        String message = root.getMessage() != null ? root.getMessage().toLowerCase() : "";

        if (root instanceof SQLException ||
            message.contains("unique") ||
            message.contains("duplicate") ||
            message.contains("constraint")) {

            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.CONFLICT.value());
            body.put("error", "Conflicto de datos");
            body.put("message", "Violación de integridad: " + root.getMessage());
            body.put("path", request.getDescription(false).replace("uri=", ""));
            return new ResponseEntity<>(body, HttpStatus.CONFLICT);
        }

        return handleGenericException(ex, request);
    }

    /** ⚠️ Maneja conflictos lógicos (por ejemplo: usuario ya existe) */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflicto lógico");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /** 💥 Maneja cualquier otro error inesperado del sistema */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error interno del servidor");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** 🔍 Obtiene la causa raíz de una excepción anidada. */
    private Throwable getRootCause(Throwable ex) {
        Throwable cause = ex.getCause();
        if (cause == null || cause == ex) return ex;
        return getRootCause(cause);
    }
}
