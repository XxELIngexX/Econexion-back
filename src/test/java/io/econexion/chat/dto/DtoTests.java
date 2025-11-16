package io.econexion.chat.dto;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class DtoTests {

    @Test
    void testMessageDTO() {
        Instant now = Instant.now();
        MessageDTO dto = new MessageDTO(1L, 10L, "Hola", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getSenderId());
        assertEquals("Hola", dto.getText());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void testConversationSummaryDTO() {
        Instant now = Instant.now();
        ConversationSummaryDTO dto =
                new ConversationSummaryDTO(1L, 2L, 3L, 4L, now, "preview");

        assertEquals(1L, dto.getConversationId());
        assertEquals("preview", dto.getPreview());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testCreateConversationRequest() {
        CreateConversationRequest req = new CreateConversationRequest();
        req.setOfferId(1L);
        req.setSenderId(2L);
        req.setReceiverId(3L);
        req.setFirstMessage("Hola!");

        assertEquals(1L, req.getOfferId());
        assertEquals(2L, req.getSenderId());
        assertEquals(3L, req.getReceiverId());
        assertEquals("Hola!", req.getFirstMessage());
    }

    @Test
    void testSendMessageRequest() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(10L);
        req.setText("Mensaje!");

        assertEquals(10L, req.getSenderId());
        assertEquals("Mensaje!", req.getText());
    }
}
