package io.econexion.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreatePostDtoTest {

    @Test
    void gettersSetters() {
        CreatePostDto dto = new CreatePostDto();
        dto.setTitle("t");
        dto.setMaterial("glass");
        dto.setQuantity(2.5);
        dto.setPrice(10.0);
        dto.setLocation("Bogotá");
        dto.setDescription("desc");

        assertEquals("t", dto.getTitle());
        assertEquals("glass", dto.getMaterial());
        assertEquals(2.5, dto.getQuantity(), 0.0001);
        assertEquals(10.0, dto.getPrice(), 0.0001);
        assertEquals("Bogotá", dto.getLocation());
        assertEquals("desc", dto.getDescription());
    }
}
