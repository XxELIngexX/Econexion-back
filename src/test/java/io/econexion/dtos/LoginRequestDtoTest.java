package io.econexion.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDtoTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("user@mail.com");
        dto.setPassword("12345");

        assertEquals("user@mail.com", dto.getEmail());
        assertEquals("12345", dto.getPassword());
    }

    @Test
    void constructor_setsValues() {
        LoginRequestDto dto = new LoginRequestDto("a@b.com", "xyz");
        assertEquals("a@b.com", dto.getEmail());
        assertEquals("xyz", dto.getPassword());
    }
}
