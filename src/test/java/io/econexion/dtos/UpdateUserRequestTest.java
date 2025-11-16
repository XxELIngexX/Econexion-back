package io.econexion.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {

    @Test
    void gettersSetters_workCorrectly() {
        UpdateUserRequest dto = new UpdateUserRequest();

        dto.setName("Daniel");
        dto.setPhone("12345");
        dto.setAddress("Bogotá");

        assertEquals("Daniel", dto.getName());
        assertEquals("12345", dto.getPhone());
        assertEquals("Bogotá", dto.getAddress());
    }

    @Test
    void allArgsConstructor_initializesFields() {
        UpdateUserRequest dto = new UpdateUserRequest(
                "Alejo",
                "3000000000",
                "Cali"
        );

        assertEquals("Alejo", dto.getName());
        assertEquals("3000000000", dto.getPhone());
        assertEquals("Cali", dto.getAddress());
    }

    @Test
    void defaultConstructor_initializesNulls() {
        UpdateUserRequest dto = new UpdateUserRequest();

        assertNull(dto.getName());
        assertNull(dto.getPhone());
        assertNull(dto.getAddress());
    }

    @Test
    void fieldComparison_insteadOfEquals() {
        UpdateUserRequest a = new UpdateUserRequest();
        a.setName("A");
        a.setPhone("B");
        a.setAddress("C");

        UpdateUserRequest b = new UpdateUserRequest();
        b.setName("A");
        b.setPhone("B");
        b.setAddress("C");

        // comparamos campo a campo
        assertEquals(a.getName(), b.getName());
        assertEquals(a.getPhone(), b.getPhone());
        assertEquals(a.getAddress(), b.getAddress());

        // cambio en un campo → ya no coinciden
        b.setAddress("otro");
        assertNotEquals(a.getAddress(), b.getAddress());
    }

    @Test
    void toString_notEmpty() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName("Test");
        dto.setPhone("1122");
        dto.setAddress("Somewhere");

        String text = dto.toString();
        assertNotNull(text);
        assertFalse(text.isBlank());
    }
}
