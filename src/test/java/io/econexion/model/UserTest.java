package io.econexion.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructor_setsFields() {
        User u = new User("Ent", "User", "123", "mail@test.com", "pass", "ADMIN");

        assertEquals("Ent", u.getEnterpriseName());
        assertEquals("User", u.getUsername());
        assertEquals("123", u.getNit());
        assertEquals("mail@test.com", u.getEmail());
        assertEquals("pass", u.getPassword());
        assertEquals("ADMIN", u.getRol());
    }

    @Test
    void settersGetters() {
        User u = new User();

        u.setEnterpriseName("E");
        u.setUsername("U");
        u.setNit("N");
        u.setEmail("e@mail.com");
        u.setPassword("p");
        u.setRol("SELLER");

        assertEquals("E", u.getEnterpriseName());
        assertEquals("U", u.getUsername());
        assertEquals("N", u.getNit());
        assertEquals("e@mail.com", u.getEmail());
        assertEquals("p", u.getPassword());
        assertEquals("SELLER", u.getRol());
    }

    @Test
    void relationshipsWork() {
        User u = new User();
        Conversation c = new Conversation();
        Offer o = new Offer();
        Post p = new Post();

        u.getConversations().add(c);
        u.getOffers().add(o);
        u.getPublications().add(p);

        assertEquals(1, u.getConversations().size());
        assertEquals(1, u.getOffers().size());
        assertEquals(1, u.getPublications().size());
    }

    @Test
    void equalsHashCode_onlyId() {
        UUID id = UUID.randomUUID();

        User a = new User();
        a.setId(id);

        User b = new User();
        b.setId(id);

        User c = new User();
        c.setId(UUID.randomUUID());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void toString_notNull() {
        User u = new User();
        u.setId(UUID.randomUUID());
        assertNotNull(u.toString());
    }
}
