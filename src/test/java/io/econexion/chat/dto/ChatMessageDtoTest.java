package io.econexion.chat.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatMessageDtoTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        ChatMessageDto dto = new ChatMessageDto();

        dto.setConversationId("conv-123");
        dto.setSenderEmail("sender@mail.com");
        dto.setReceiverEmail("receiver@mail.com");
        dto.setContent("Hola, ¿cómo estás?");

        assertEquals("conv-123", dto.getConversationId());
        assertEquals("sender@mail.com", dto.getSenderEmail());
        assertEquals("receiver@mail.com", dto.getReceiverEmail());
        assertEquals("Hola, ¿cómo estás?", dto.getContent());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        ChatMessageDto dto = new ChatMessageDto(
                "conv-999",
                "a@mail.com",
                "b@mail.com",
                "Contenido"
        );

        assertEquals("conv-999", dto.getConversationId());
        assertEquals("a@mail.com", dto.getSenderEmail());
        assertEquals("b@mail.com", dto.getReceiverEmail());
        assertEquals("Contenido", dto.getContent());
    }

    @Test
    void defaultConstructor_initializesToNulls() {
        ChatMessageDto dto = new ChatMessageDto();

        assertNull(dto.getConversationId());
        assertNull(dto.getSenderEmail());
        assertNull(dto.getReceiverEmail());
        assertNull(dto.getContent());
    }

    @Test
    void equalsHashCodeAndToString_workCorrectly() {
        ChatMessageDto a = new ChatMessageDto(
                "c1", "u1@mail.com", "u2@mail.com", "hola"
        );
        ChatMessageDto b = new ChatMessageDto(
                "c1", "u1@mail.com", "u2@mail.com", "hola"
        );

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        String ts = a.toString();
        assertTrue(ts.contains("c1"));
        assertTrue(ts.contains("u1@mail.com"));
        assertTrue(ts.contains("hola"));

        b.setContent("otro");
        assertNotEquals(a, b);
    }
}
