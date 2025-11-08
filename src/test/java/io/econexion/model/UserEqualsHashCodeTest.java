package io.econexion.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserEqualsHashCodeTest {

    private User withId(UUID id) {
        User u = new User();
        u.setId(id);
        u.setEmail("x@y.com");
        u.setPassword("p");
        return u;
    }

    @Test
    @DisplayName("Mismo ID ⇒ equals true y mismo hashCode")
    void same_id_equals_and_hash() {
        UUID id = UUID.randomUUID();
        User a = withId(id);
        User b = withId(id);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("IDs distintos ⇒ equals false")
    void different_id_not_equals() {
        User a = withId(UUID.randomUUID());
        User b = withId(UUID.randomUUID());

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("toString no debe ser vacío")
    void toString_not_empty() {
        User u = withId(UUID.randomUUID());
        assertNotNull(u.toString());
        assertFalse(u.toString().isBlank());
    }
}
