package io.econexion.chat.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MessageDTOTest {

    @Test
    void allArgsConstructor_setsFieldsCorrectly() {
        Instant now = Instant.now();
        MessageDTO dto = new MessageDTO(1L, 2L, "Hola", now);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getSenderId());
        assertEquals("Hola", dto.getText());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void fieldComparison_insteadOfEquals() {
        Instant now = Instant.now();

        MessageDTO a = new MessageDTO(10L, 20L, "Msg", now);
        MessageDTO b = new MessageDTO(10L, 20L, "Msg", now);

        // Comparamos campo a campo en vez de usar equals()
        assertEquals(a.getId(),         b.getId());
        assertEquals(a.getSenderId(),   b.getSenderId());
        assertEquals(a.getText(),       b.getText());
        assertEquals(a.getCreatedAt(),  b.getCreatedAt());

        // Cambiamos un valor en otro objeto → ya no coinciden los campos
        MessageDTO c = new MessageDTO(99L, 20L, "Msg", now);
        assertNotEquals(a.getId(), c.getId());
    }

    @Test
    void toString_notNullOrBlank() {
        MessageDTO dto = new MessageDTO(1L, 2L, "Test", Instant.now());

        String ts = dto.toString();
        assertNotNull(ts);
        assertFalse(ts.isBlank());
    }
}
