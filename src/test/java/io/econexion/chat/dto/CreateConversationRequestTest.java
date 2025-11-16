package io.econexion.chat.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateConversationRequestTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        CreateConversationRequest dto = new CreateConversationRequest();

        dto.setOfferId(10L);
        dto.setSenderId(20L);
        dto.setReceiverId(30L);
        dto.setFirstMessage("Hola!");

        assertEquals(10L, dto.getOfferId());
        assertEquals(20L, dto.getSenderId());
        assertEquals(30L, dto.getReceiverId());
        assertEquals("Hola!", dto.getFirstMessage());
    }

    @Test
    void defaultConstructor_initializesNulls() {
        CreateConversationRequest dto = new CreateConversationRequest();

        assertNull(dto.getOfferId());
        assertNull(dto.getSenderId());
        assertNull(dto.getReceiverId());
        assertNull(dto.getFirstMessage());
    }

    @Test
    void fieldComparison_insteadOfEquals() {
        CreateConversationRequest a = new CreateConversationRequest();
        a.setOfferId(1L);
        a.setSenderId(2L);
        a.setReceiverId(3L);
        a.setFirstMessage("Msg");

        CreateConversationRequest b = new CreateConversationRequest();
        b.setOfferId(1L);
        b.setSenderId(2L);
        b.setReceiverId(3L);
        b.setFirstMessage("Msg");

        // Comparamos campo a campo
        assertEquals(a.getOfferId(), b.getOfferId());
        assertEquals(a.getSenderId(), b.getSenderId());
        assertEquals(a.getReceiverId(), b.getReceiverId());
        assertEquals(a.getFirstMessage(), b.getFirstMessage());

        // Cambiar un campo debe romper la igualdad de ese campo
        b.setSenderId(99L);
        assertNotEquals(a.getSenderId(), b.getSenderId());
    }

    @Test
    void toString_notEmpty() {
        CreateConversationRequest dto = new CreateConversationRequest();
        dto.setOfferId(10L);
        dto.setSenderId(20L);
        dto.setReceiverId(30L);
        dto.setFirstMessage("Hola!");

        String ts = dto.toString();
        assertNotNull(ts);
        assertFalse(ts.isBlank());
    }
}
