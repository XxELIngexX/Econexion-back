package io.econexion.service;

import io.econexion.model.User;
import io.econexion.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 🧩 Inicializa un usuario administrador por defecto al iniciar la aplicación.
     */
    @PostConstruct
    public void init() {
        repository.findByEmail("admin@econexia.admin").orElseGet(() -> {
            User newUser = new User(
                    "Econexia",
                    "administrativo",
                    "123456789",
                    "admin@econexia.admin",
                    passwordEncoder.encode("admin1234"),
                    "admin"
            );
            return repository.save(newUser);
        });
    }

    // === CRUD BÁSICO ===

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * 🧠 Crea un nuevo usuario con validaciones básicas y encriptación de contraseña.
     * Lanza IllegalStateException si el email ya existe (409 esperado).
     */
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        // ❌ Verificar duplicado
        repository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("El email ya está registrado");
        });

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    /**
     * 🔄 Actualiza un usuario existente por ID.
     */
    public Optional<User> update(UUID id, User newUser) {
        if (id == null || newUser == null) return Optional.empty();

        return repository.findById(id).map(existing -> {
            existing.setEnterpriseName(newUser.getEnterpriseName());
            existing.setUsername(newUser.getUsername());
            existing.setNit(newUser.getNit());
            existing.setEmail(newUser.getEmail());
            existing.setRol(newUser.getRol());

            if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(newUser.getPassword()));
            }

            return repository.save(existing);
        });
    }

    /**
     * 🗑️ Elimina un usuario si existe.
     */
    public boolean delete(UUID id) {
        if (id == null || !repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    /**
     * ⚙️ Actualiza directamente un usuario (para pruebas).
     */
    public User update(User user) {
        if (user == null) throw new IllegalArgumentException("El usuario no puede ser null");

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return repository.save(user);
    }
}
