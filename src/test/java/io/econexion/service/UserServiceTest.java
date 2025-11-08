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
        base.setEnterpriseName("Econex");
        base.setNit("999");
        base.setRol("USER");
    }

    // ✅ Caso 1: No se puede crear si el email ya existe
    @Test
    void create_whenEmailAlreadyExists_thenThrows() {
        when(repository.findByEmail("unique@mail.com"))
                .thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(IllegalStateException.class, () -> service.create(base));

        assertNotNull(ex.getMessage(), "Debe lanzar excepción con mensaje");
        verify(repository, never()).save(any());
    }

    // ✅ Caso 2: Se crea correctamente con email nuevo (codifica password y guarda)
    @Test
    void create_whenNewEmail_thenEncodesPasswordAndSaves() {
        when(repository.findByEmail("unique@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-pass");
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

    // 🔄 UPDATE: retorna Optional vacío si no existe
    @Test
    void update_whenIdNotFound_returnsEmpty() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = service.update(id, base);

        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    // 🔄 UPDATE: actualiza campos y codifica password si viene
    @Test
    void update_whenFound_updatesFields_andEncodesPasswordIfPresent() {
        UUID id = UUID.randomUUID();
        User existing = new User();
        existing.setId(id);
        existing.setEmail("old@mail.com");
        existing.setPassword("old-enc");
        existing.setUsername("Old");
        existing.setEnterpriseName("OldEnt");
        existing.setNit("111");
        existing.setRol("USER");

        User incoming = new User();
        incoming.setEnterpriseName("NewEnt");
        incoming.setUsername("NewUser");
        incoming.setNit("222");
        incoming.setEmail("new@mail.com");
        incoming.setPassword("new-raw");
        incoming.setRol("ADMIN");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("new-raw")).thenReturn("new-enc");
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> updated = service.update(id, incoming);

        assertTrue(updated.isPresent());
        User u = updated.get();
        assertEquals("NewEnt", u.getEnterpriseName());
        assertEquals("NewUser", u.getUsername());
        assertEquals("222", u.getNit());
        assertEquals("new@mail.com", u.getEmail());
        assertEquals("ADMIN", u.getRol());
        assertEquals("new-enc", u.getPassword());
        verify(passwordEncoder).encode("new-raw");
        verify(repository).save(any(User.class));
    }

    // 🔄 UPDATE: si password es blanco/nulo, no recodifica
    @Test
    void update_whenPasswordBlank_doesNotEncode() {
        UUID id = UUID.randomUUID();
        User existing = new User();
        existing.setId(id);
        existing.setPassword("keep-enc");
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User incoming = new User();
        incoming.setPassword(""); // en blanco

        Optional<User> updated = service.update(id, incoming);

        assertTrue(updated.isPresent());
        assertEquals("keep-enc", updated.get().getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // 🗑️ DELETE: false si no existe
    @Test
    void delete_whenNotExists_returnsFalse() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertFalse(service.delete(id));
        verify(repository, never()).deleteById(any());
    }

    // 🗑️ DELETE: true si existe
    @Test
    void delete_whenExists_deletesAndReturnsTrue() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        assertTrue(service.delete(id));
        verify(repository).deleteById(id);
    }

    // 🛠️ update(entity directo): codifica si viene password
    @Test
    void updateEntity_direct_encodesIfPasswordProvided() {
        User u = new User();
        u.setPassword("plain");
        when(passwordEncoder.encode("plain")).thenReturn("enc");
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = service.update(u);

        assertEquals("enc", saved.getPassword());
        verify(passwordEncoder).encode("plain");
        verify(repository).save(any(User.class));
    }

    @Test
    void updateEntity_direct_keepsPasswordIfBlank() {
        User u = new User();
        u.setPassword("");

        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = service.update(u);

        assertEquals("", saved.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
