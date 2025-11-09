package io.econexion.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ConversationModelValidationTest {

    @Test
    void equalsHashCode_sameId_shouldBeEqual() {
        UUID id = UUID.randomUUID();
        Conversation a = new Conversation();
        a.setId(id);
        Conversation b = new Conversation();
        b.setId(id);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsHashCode_differentId_shouldNotBeEqual() {
        Conversation a = new Conversation();
        Conversation b = new Conversation();
        a.setId(UUID.randomUUID());
        b.setId(UUID.randomUUID());
        assertNotEquals(a, b);
    }
}
