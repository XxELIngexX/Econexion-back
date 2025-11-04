package io.econexion.repository;

import io.econexion.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // 🔧 Helper para crear usuarios de prueba
    private User buildUser(String email, String username) {
        User u = new User();
        u.setEnterpriseName("Acme Corp");
        u.setUsername(username);
        u.setNit("123456789");
        u.setEmail(email);
        u.setPassword("secret");
        u.setRol("BUYER");
        return u;
    }

    @Test
    @DisplayName("save → findByEmail → delete (flujo feliz)")
    void save_findByEmail_delete() {
        User saved = userRepository.save(buildUser("repo@test.com", "acme-user"));
        assertNotNull(saved.getId(), "JPA debe asignar UUID");

        Optional<User> found = userRepository.findByEmail("repo@test.com");
        assertTrue(found.isPresent());
        assertEquals("acme-user", found.get().getUsername());

        userRepository.deleteById(saved.getId());
        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("findByEmail cuando no existe → Optional.empty()")
    void findByEmail_notFound_returnsEmpty() {
        assertTrue(userRepository.findByEmail("missing@mail.com").isEmpty());
    }

    @Test
    @DisplayName("Guardar dos usuarios con el mismo email → DataIntegrityViolationException")
    void save_duplicateEmail_throwsConstraintViolation() {
        userRepository.saveAndFlush(buildUser("dup@mail.com", "user1")); // 🔥 fuerza el primer insert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(buildUser("dup@mail.com", "user2")); // 🔥 dispara la excepción
        });
    }

    @Test
    @DisplayName("update persiste cambios en la misma entidad")
    void update_persistsChanges() {
        User u = userRepository.save(buildUser("update@test.com", "before"));
        u.setUsername("after");
        User updated = userRepository.saveAndFlush(u); // 🔥 flush asegura persistencia inmediata

        assertEquals(u.getId(), updated.getId());
        assertEquals("after", userRepository.findByEmail("update@test.com").orElseThrow().getUsername());
    }
}
