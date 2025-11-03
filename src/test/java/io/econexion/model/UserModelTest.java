package io.econexion.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class UserModelTest {

    @Test
    void gettersSettersEqualsHashCode() {
        User a = new User();
        a.setId(UUID.randomUUID());
        a.setEnterpriseName("Ent");
        a.setUsername("user");
        a.setNit("NIT");
        a.setEmail("u@ex.com");
        a.setPassword("p");
        a.setRol("SELLER");

        User b = new User();
        b.setId(a.getId());
        b.setUsername("user");
        b.setEmail("u@ex.com");

        assertEquals(a.getId(), b.getId());
        assertEquals("SELLER", a.getRol());

        // Lombok @Data -> equals/hashCode usan campos
        assertNotEquals(a, new User());
        assertNotNull(a.toString());
    }
}
