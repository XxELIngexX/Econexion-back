package io.econexion.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreatePostDtoTest {

    @Test
    void gettersSetters_work() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("user@mail.com");
        dto.setPassword("123");
        assertEquals("user@mail.com", dto.getEmail());
        assertEquals("123", dto.getPassword());
    }
}
