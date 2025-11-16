package io.econexion.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void gettersSetters_workCorrectly() {
        ErrorResponse dto = new ErrorResponse();

        dto.setMessage("Error fatal");
        dto.setDetails("Algo salió mal");

        assertEquals("Error fatal", dto.getMessage());
        assertEquals("Algo salió mal", dto.getDetails());
    }

    @Test
    void defaultConstructor_initializesNulls() {
        ErrorResponse dto = new ErrorResponse();

        assertNull(dto.getMessage());
        assertNull(dto.getDetails());
    }

    @Test
    void fieldComparison_insteadOfEquals() {
        ErrorResponse a = new ErrorResponse("Msg", "Det");
        ErrorResponse b = new ErrorResponse("Msg", "Det");

        assertEquals(a.getMessage(), b.getMessage());
        assertEquals(a.getDetails(), b.getDetails());

        b.setDetails("otro");
        assertNotEquals(a.getDetails(), b.getDetails());
    }

    @Test
    void toString_notEmpty() {
        ErrorResponse dto = new ErrorResponse("M", "D");

        assertNotNull(dto.toString());
        assertFalse(dto.toString().isBlank());
    }
}
