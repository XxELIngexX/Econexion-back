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

    @Test
    void fieldComparison_insteadOfEquals() {
        UUID id = UUID.randomUUID();

        CreateOfferDTO a = new CreateOfferDTO();
        a.setPublicationId(id);
        a.setAmount(10.0);
        a.setMessage("msg");

        CreateOfferDTO b = new CreateOfferDTO();
        b.setPublicationId(id);
        b.setAmount(10.0);
        b.setMessage("msg");

        // Compare fields manually (no Lombok)
        assertEquals(a.getPublicationId(), b.getPublicationId());
        assertEquals(a.getAmount(), b.getAmount(), 0.0001);
        assertEquals(a.getMessage(), b.getMessage());

        // Change one field → should now differ
        b.setAmount(99.0);
        assertNotEquals(a.getAmount(), b.getAmount());
    }
}
