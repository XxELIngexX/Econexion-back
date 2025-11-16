package io.econexion.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileResponseTest {

    @Test
    void gettersSetters_workCorrectly() {
        UserProfileResponse dto = new UserProfileResponse();

        dto.setId(5L);
        dto.setName("Daniel");
        dto.setEmail("daniel@mail.com");
        dto.setPhone("98765");
        dto.setAddress("Cali");

        assertEquals(5L, dto.getId());
        assertEquals("Daniel", dto.getName());
        assertEquals("daniel@mail.com", dto.getEmail());
        assertEquals("98765", dto.getPhone());
        assertEquals("Cali", dto.getAddress());
    }

    @Test
    void allArgsConstructor_initializesFields() {
        UserProfileResponse dto = new UserProfileResponse(
                10L,
                "Alejo",
                "alejo@mail.com",
                "3001234567",
                "Bogotá"
        );

        assertEquals(10L, dto.getId());
        assertEquals("Alejo", dto.getName());
        assertEquals("alejo@mail.com", dto.getEmail());
        assertEquals("3001234567", dto.getPhone());
        assertEquals("Bogotá", dto.getAddress());
    }

    @Test
    void defaultConstructor_initializesNulls() {
        UserProfileResponse dto = new UserProfileResponse();

        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhone());
        assertNull(dto.getAddress());
    }

    @Test
    void fieldComparison_insteadOfEquals() {
        UserProfileResponse a = new UserProfileResponse();
        a.setId(1L);
        a.setName("A");
        a.setEmail("B");
        a.setPhone("C");
        a.setAddress("D");

        UserProfileResponse b = new UserProfileResponse();
        b.setId(1L);
        b.setName("A");
        b.setEmail("B");
        b.setPhone("C");
        b.setAddress("D");

        assertEquals(a.getId(), b.getId());
        assertEquals(a.getName(), b.getName());
        assertEquals(a.getEmail(), b.getEmail());
        assertEquals(a.getPhone(), b.getPhone());
        assertEquals(a.getAddress(), b.getAddress());

        b.setEmail("otro@mail.com");
        assertNotEquals(a.getEmail(), b.getEmail());
    }

    @Test
    void toString_notEmpty() {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(88L);
        dto.setName("Z");
        dto.setEmail("mail@example.com");

        String text = dto.toString();
        assertNotNull(text);
        assertFalse(text.isBlank());
    }
}
