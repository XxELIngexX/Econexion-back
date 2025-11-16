package io.econexion.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    @Test
    void testGettersSetters() {
        Conversation c = new Conversation();

        UUID id = UUID.randomUUID();
        Offer offer = new Offer();
        User u1 = new User();
        User u2 = new User();
        Message m1 = new Message();

        c.setId(id);
        c.setOffer(offer);
        c.getParticipants().add(u1);
        c.getParticipants().add(u2);
        c.getMessages().add(m1);

        assertEquals(id, c.getId());
        assertEquals(offer, c.getOffer());
        assertEquals(2, c.getParticipants().size());
        assertEquals(1, c.getMessages().size());
    }

    @Test
    void equalsHashCode_shouldWork() {
        UUID id = UUID.randomUUID();

        Conversation a = new Conversation();
        a.setId(id);

        Conversation b = new Conversation();
        b.setId(id);

        Conversation c = new Conversation();
        c.setId(UUID.randomUUID());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void toString_notNull() {
        Conversation c = new Conversation();
        c.setId(UUID.randomUUID());
        assertNotNull(c.toString());
    }
}
