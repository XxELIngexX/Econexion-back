package io.econexion.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void constructorFull_setsFields() {
        User u = new User();
        Post p = new Post("Iron", 10.0, 50.0, "Bogotá", "Desc", u);

        assertEquals("Iron", p.getMaterial());
        assertEquals(10.0, p.getQuantity());
        assertEquals(50.0, p.getPrice());
        assertEquals("Bogotá", p.getLocation());
        assertEquals("Desc", p.getDescription());
        assertEquals(u, p.getOwner());
    }

    @Test
    void constructorBasic_setsFields() {
        User u = new User();
        Post p = new Post("Title", "Content", u);

        assertEquals("Title", p.getMaterial());
        assertEquals("Content", p.getDescription());
        assertEquals(u, p.getOwner());
    }

    @Test
    void gettersSetters() {
        Post p = new Post();
        p.setTitle("T");
        p.setContent("C");
        p.setMaterial("M");
        p.setPrice(4.0);

        assertEquals("T", p.getTitle());
        assertEquals("C", p.getContent());
        assertEquals("M", p.getMaterial());
        assertEquals(4.0, p.getPrice());
    }

    @Test
    void toString_notNull() {
        Post p = new Post();
        p.setId(UUID.randomUUID());
        assertNotNull(p.toString());
    }
}
