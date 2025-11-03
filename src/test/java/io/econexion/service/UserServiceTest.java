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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User base;

    @BeforeEach
    void setUp() {
        base = new User();
        base.setId(UUID.randomUUID());
        base.setEmail("unique@mail.com");
        base.setPassword("raw-password");
    }

    @Test
    void create_whenEmailAlreadyExists_thenThrows() {
        when(repository.findByEmail("unique@mail.com"))
                .thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(Exception.class, () -> service.create(base));

        assertTrue(ex.getMessage().toLowerCase().contains("existe"));
        verify(repository, never()).save(any());
    }

    @Test
    void create_whenNewEmail_thenSaves() throws Exception {
        when(repository.findByEmail("unique@mail.com"))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    if (u.getId() == null) u.setId(UUID.randomUUID());
                    return u;
                });

        User out = service.create(base);

        assertNotNull(out.getId());
        assertEquals("unique@mail.com", out.getEmail());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        assertEquals("unique@mail.com", captor.getValue().getEmail());
    }
}
