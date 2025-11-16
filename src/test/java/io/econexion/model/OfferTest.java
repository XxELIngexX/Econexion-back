package io.econexion.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferTest {

    @Test
    void constructor1_setsFields() {
        User u = new User();
        Post p = new Post();

        Offer o = new Offer("A", "B", 10.0, u, p);

        assertEquals("A", o.getTitle());
        assertEquals("B", o.getDescription());
        assertEquals(10.0, o.getPrice());
        assertEquals(10.0, o.getAmount());
        assertEquals("B", o.getMessage());
        assertEquals(u, o.getOfferer());
        assertEquals(p, o.getPublication());
    }

    @Test
    void constructor2_setsFields() {
        User u = new User();
        Post p = new Post();

        Offer o = new Offer(40.0, "Msg", u, p);

        assertEquals(40.0, o.getPrice());
        assertEquals(40.0, o.getAmount());
        assertEquals("Msg", o.getDescription());
        assertEquals("Msg", o.getMessage());
        assertEquals("Offer", o.getTitle());
        assertEquals(u, o.getOfferer());
        assertEquals(p, o.getPublication());
    }

    @Test
    void equalsHashCode_onlyId() {
        UUID id = UUID.randomUUID();

        Offer a = new Offer();
        a.setId(id);

        Offer b = new Offer();
        b.setId(id);

        Offer c = new Offer();
        c.setId(UUID.randomUUID());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void settersWork() {
        Offer o = new Offer();
        o.setTitle("T");
        o.setDescription("D");
        o.setAmount(5.0);
        o.setMessage("M");

        assertEquals("T", o.getTitle());
        assertEquals("D", o.getDescription());
        assertEquals(5.0, o.getAmount());
        assertEquals("M", o.getMessage());
    }
}
