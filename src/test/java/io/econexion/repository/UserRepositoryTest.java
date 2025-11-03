package io.econexion.repository;

import io.econexion.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void save_findByEmail_delete() {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setEnterpriseName("Acme Corp");
        u.setUsername("acme-user");
        u.setNit("123456789");
        u.setEmail("repo@test.com");
        u.setPassword("secret");
        u.setRol("BUYER");

        User saved = userRepository.save(u);
        assertNotNull(saved);

        Optional<User> found = userRepository.findByEmail("repo@test.com");
        assertTrue(found.isPresent());
        assertEquals("acme-user", found.get().getUsername());

        userRepository.deleteById(saved.getId());
        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }
}
