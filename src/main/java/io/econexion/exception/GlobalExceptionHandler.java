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
 * Global exception handler for providing consistent and structured JSON responses
 * when exceptions occur across the entire application.
 * <p>
 * Extends {@link ResponseEntityExceptionHandler} to override validation handling,
 * and provides custom handlers for common backend errors such as constraint violations,
 * logical conflicts, and unexpected system failures.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles validation errors triggered by @Valid annotations.
     * <p>
     * Extracts field-level validation messages and returns a structured
     * response with HTTP 400 (Bad Request).
     * </p>
     *
     * @param ex      the thrown validation exception
     * @param headers default HTTP headers
     * @param status  default HTTP status
     * @param request the web request context
     * @return structured JSON response with validation errors
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");

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

    /**
     * Handles data integrity violations such as duplicate keys or constraint violations.
     * <p>
     * Returns HTTP 409 (Conflict) when database-level constraints are triggered.
     * </p>
     *
     * @param ex      the thrown DataIntegrityViolationException
     * @param request the web request context
     * @return JSON response describing the conflict
     */
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
            body.put("error", "Data conflict");
            body.put("message", "Integrity violation: " + root.getMessage());
            body.put("path", request.getDescription(false).replace("uri=", ""));

            return new ResponseEntity<>(body, HttpStatus.CONFLICT);
        }

        return handleGenericException(ex, request);
    }

    /**
     * Handles logical conflicts raised via IllegalStateException.
     * <p>
     * This is typically used for domain-specific business rules (e.g., user already exists).
     * Returns HTTP 409 (Conflict).
     * </p>
     *
     * @param ex      the thrown IllegalStateException
     * @param request the web request context
     * @return JSON response describing the conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Logical conflict");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /**
     * Generic fallback exception handler for unexpected runtime errors.
     * <p>
     * Returns HTTP 500 (Internal Server Error) with message and path info.
     * </p>
     *
     * @param ex      the thrown exception
     * @param request the web request context
     * @return JSON response for unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Retrieves the root cause of a nested exception recursively.
     *
     * @param ex the thrown exception
     * @return the deepest underlying cause
     */
    private Throwable getRootCause(Throwable ex) {
        Throwable cause = ex.getCause();
        if (cause == null || cause == ex) return ex;
        return getRootCause(cause);
    }
}
