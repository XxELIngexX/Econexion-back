package io.econexion.dtos;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateOfferDTOTest {

    @Test
    void gettersSetters() {
        CreateOfferDTO dto = new CreateOfferDTO();
        UUID pid = UUID.randomUUID();
        dto.setPublicationId(pid);
        dto.setAmount(123.45);
        dto.setMessage("hola");

        assertEquals(pid, dto.getPublicationId());
        assertEquals(123.45, dto.getAmount(), 0.0001);
        assertEquals("hola", dto.getMessage());
    }

    @Test
    void defaultValues_areNullOrZero() {
        CreateOfferDTO dto = new CreateOfferDTO();

        assertNull(dto.getPublicationId());
        assertEquals(0.0, dto.getAmount(), 0.0001);
        assertNull(dto.getMessage());
    }
}
