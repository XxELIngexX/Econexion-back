package io.econexion.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Generic error response DTO used to return structured error information
 * to API clients when an exception or validation issue occurs.
 * <p>
 * This object typically contains a short message describing the error
 * and additional details that provide context about what went wrong.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * High-level error message presented to the client.
     */
    private String message;

    /**
     * Additional details explaining the nature or cause of the error.
     */
    private String details;
}
