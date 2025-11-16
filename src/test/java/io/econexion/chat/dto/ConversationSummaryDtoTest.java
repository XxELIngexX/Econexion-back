package io.econexion.chat.dto;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class ConversationSummaryDTOTest {

    @Test
    void allArgsConstructor_setsAllFields() {
        Instant now = Instant.now();

        ConversationSummaryDTO dto = new ConversationSummaryDTO(
                1L, 2L, 3L, 4L, now, "preview"
        );

        assertEquals(1L, dto.getConversationId());
        assertEquals(2L, dto.getOfferId());
        assertEquals(3L, dto.getParticipant1Id());
        assertEquals(4L, dto.getParticipant2Id());
        assertEquals(now, dto.getUpdatedAt());
        assertEquals("preview", dto.getLastMessagePreview());
        assertEquals("preview", dto.getPreview());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        ConversationSummaryDTO dto = new ConversationSummaryDTO();
        Instant now = Instant.now();

        dto.setConversationId(10L);
        dto.setOfferId(20L);
        dto.setParticipant1Id(30L);
        dto.setParticipant2Id(40L);
        dto.setUpdatedAt(now);
        dto.setLastMessagePreview("hola");

        assertEquals(10L, dto.getConversationId());
        assertEquals(20L, dto.getOfferId());
        assertEquals(30L, dto.getParticipant1Id());
        assertEquals(40L, dto.getParticipant2Id());
        assertEquals(now, dto.getUpdatedAt());
        assertEquals("hola", dto.getLastMessagePreview());
        assertEquals("hola", dto.getPreview());
    }

    @Test
    void equalsHashCodeToString_workCorrectly() {
        Instant now = Instant.now();

        ConversationSummaryDTO a =
                new ConversationSummaryDTO(1L, 2L, 3L, 4L, now, "x");

        ConversationSummaryDTO b =
                new ConversationSummaryDTO(1L, 2L, 3L, 4L, now, "x");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        String ts = a.toString();
        assertTrue(ts.contains("1"));
        assertTrue(ts.contains("x"));

        b.setLastMessagePreview("zzz");
        assertNotEquals(a, b);
    }
}
