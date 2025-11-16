package io.econexion.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testGettersSetters() {
        Message m = new Message();

        UUID id = UUID.randomUUID();
        User sender = new User();
        Conversation conv = new Conversation();

        m.setId(id);
        m.setText("hola!");
        m.setSender(sender);
        m.setConversation(conv);

        assertEquals(id, m.getId());
        assertEquals("hola!", m.getText());
        assertEquals(sender, m.getSender());
        assertEquals(conv, m.getConversation());
        assertNotNull(m.getDate()); // fecha automática
    }

    @Test
    void toString_notNull() {
        Message m = new Message();
        m.setId(UUID.randomUUID());
        m.setText("Test");
        assertNotNull(m.toString());
    }
}
