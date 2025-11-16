package io.econexion.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreatePostDtoTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        CreatePostDto dto = new CreatePostDto();

        dto.setTitle("Material reciclable");
        dto.setMaterial("Plástico");
        dto.setQuantity(10.5);
        dto.setPrice(2500.75);
        dto.setLocation("Bogotá");
        dto.setDescription("Plástico reciclado en buen estado");

        assertEquals("Material reciclable", dto.getTitle());
        assertEquals("Plástico", dto.getMaterial());
        assertEquals(10.5, dto.getQuantity(), 0.0001);
        assertEquals(2500.75, dto.getPrice(), 0.0001);
        assertEquals("Bogotá", dto.getLocation());
        assertEquals("Plástico reciclado en buen estado", dto.getDescription());
    }

    @Test
    void defaultValues_areNullOrZero() {
        CreatePostDto dto = new CreatePostDto();

        assertNull(dto.getTitle());
        assertNull(dto.getMaterial());
        assertEquals(0.0, dto.getQuantity(), 0.0001);
        assertEquals(0.0, dto.getPrice(), 0.0001);
        assertNull(dto.getLocation());
        assertNull(dto.getDescription());
    }

    @Test
    void fieldComparison_insteadOfEquals() {
        CreatePostDto a = new CreatePostDto();
        a.setTitle("T");
        a.setMaterial("M");
        a.setQuantity(1.1);
        a.setPrice(2.2);
        a.setLocation("L");
        a.setDescription("D");

        CreatePostDto b = new CreatePostDto();
        b.setTitle("T");
        b.setMaterial("M");
        b.setQuantity(1.1);
        b.setPrice(2.2);
        b.setLocation("L");
        b.setDescription("D");

        assertEquals(a.getTitle(), b.getTitle());
        assertEquals(a.getMaterial(), b.getMaterial());
        assertEquals(a.getQuantity(), b.getQuantity(), 0.0001);
        assertEquals(a.getPrice(), b.getPrice(), 0.0001);
        assertEquals(a.getLocation(), b.getLocation());
        assertEquals(a.getDescription(), b.getDescription());

        b.setTitle("X");
        assertNotEquals(a.getTitle(), b.getTitle());
    }
}
