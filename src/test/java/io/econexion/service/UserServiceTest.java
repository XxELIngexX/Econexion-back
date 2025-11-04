package io.econexion.service;

import io.econexion.model.User;
import io.econexion.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    private User base;

    @BeforeEach
    void setUp() {
        base = new User();
        base.setId(UUID.randomUUID());
        base.setEmail("unique@mail.com");
        base.setPassword("raw-password");
        base.setUsername("TestUser");
    }

    // ✅ Caso 1: No se puede crear si el email ya existe
    @Test
    void create_whenEmailAlreadyExists_thenThrows() {
        when(repository.findByEmail("unique@mail.com"))
                .thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(IllegalStateException.class, () -> service.create(base));

        assertTrue(ex.getMessage().toLowerCase().contains("existe"));
        verify(repository, never()).save(any());
    }

    // ✅ Caso 2: Se crea correctamente con email nuevo
    @Test
    void create_whenNewEmail_thenEncodesPasswordAndSaves() {
        when(repository.findByEmail("unique@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null) u.setId(UUID.randomUUID());
            return u;
        });

        User result = service.create(base);

        assertNotNull(result.getId());
        assertEquals("unique@mail.com", result.getEmail());
        assertNotEquals("raw-password", result.getPassword()); // se codificó
        verify(passwordEncoder).encode("raw-password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        assertEquals("encoded-pass", captor.getValue().getPassword());
    }

    // ✅ Caso 3: Validación de nulos o entradas inválidas
    @Test
    void create_whenUserIsNull_thenThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_whenEmailIsMissing_thenThrows() {
        User invalid = new User();
        invalid.setPassword("123");
        assertThrows(IllegalArgumentException.class, () -> service.create(invalid));
    }

    @Test
    void create_whenPasswordIsMissing_thenThrows() {
        User invalid = new User();
        invalid.setEmail("mail@test.com");
        assertThrows(IllegalArgumentException.class, () -> service.create(invalid));
    }
}
