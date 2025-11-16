package io.econexion.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void gettersSetters() {
        RegisterRequest dto = new RegisterRequest();

        dto.setEmail("daniel@example.com");
        dto.setPassword("pass1234");
        dto.setName("Daniel");

        assertEquals("daniel@example.com", dto.getEmail());
        assertEquals("pass1234", dto.getPassword());
        assertEquals("Daniel", dto.getName());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        RegisterRequest dto = new RegisterRequest(
                "user@mail.com",
                "superSecret",
                "Alejandro"
        );

        assertEquals("user@mail.com", dto.getEmail());
        assertEquals("superSecret", dto.getPassword());
        assertEquals("Alejandro", dto.getName());
    }

    @Test
    void fieldComparison_insteadOfEquals() {
        RegisterRequest a = new RegisterRequest("a@b.com", "111", "Dan");
        RegisterRequest b = new RegisterRequest("a@b.com", "111", "Dan");

        // Comparamos campo a campo (sin depender de equals/hashCode de Lombok)
        assertEquals(a.getEmail(), b.getEmail());
        assertEquals(a.getPassword(), b.getPassword());
        assertEquals(a.getName(), b.getName());

        // Si cambiamos un campo, deben diferir
        b.setEmail("x@x.com");
        assertNotEquals(a.getEmail(), b.getEmail());
    }

    @Test
    void emptyConstructor_defaultsToNull() {
        RegisterRequest dto = new RegisterRequest();

        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
        assertNull(dto.getName());
    }

    @Test
    void toString_notNullOrBlank() {
        RegisterRequest dto = new RegisterRequest("mail@test.com", "pwd", "Name");

        String ts = dto.toString();

        assertNotNull(ts);
        assertFalse(ts.isBlank());
    }
}
    