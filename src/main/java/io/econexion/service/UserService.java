package io.econexion.service;

import io.econexion.model.User;
import io.econexion.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing {@link User} entities.
 * <p>
 * This service encapsulates user-related business logic such as:
 * <ul>
 *     <li>Creating users with basic validation and password encoding</li>
 *     <li>Initializing a default administrator account</li>
 *     <li>CRUD operations and search by email/ID</li>
 * </ul>
 * It delegates persistence operations to {@link UserRepository}.
 * </p>
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a {@link UserService} with the required dependencies.
     *
     * @param repository      the user repository used for persistence
     * @param passwordEncoder encoder used for hashing user passwords
     */
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Initializes a default administrator user when the application starts.
     * <p>
     * If a user with the email {@code admin@econexia.admin} does not exist,
     * this method creates one with a predefined password and role.
     * </p>
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

    // === BASIC CRUD ===

    /**
     * Retrieves all users stored in the system.
     *
     * @return list of all {@link User} entities
     */
    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * Finds a user by its unique identifier.
     *
     * @param id UUID of the user
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Finds a user by its email address.
     *
     * @param email email to look up
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Creates a new user with basic validation and password encryption.
     * <p>
     * Validates:
     * <ul>
     *     <li>Non-null user</li>
     *     <li>Non-empty email</li>
     *     <li>Email uniqueness</li>
     *     <li>Non-empty password</li>
     * </ul>
     * The password is encoded before persisting.
     * </p>
     * <p>
     * Throws {@link IllegalStateException} if the email already exists (expected to map to HTTP 409).
     * </p>
     *
     * @param user user to create
     * @return the persisted {@link User}
     * @throws IllegalArgumentException if user or required fields are invalid
     * @throws IllegalStateException    if the email is already registered
     */
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        // Check for duplicate email
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
     * Updates an existing user by ID.
     * <p>
     * Only selected fields are updated:
     * enterpriseName, username, nit, email, role and optionally password.
     * If a non-blank password is provided, it is re-encoded.
     * </p>
     *
     * @param id      user identifier
     * @param newUser user data containing updated values
     * @return an {@link Optional} with the updated user, or empty if not found
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
     * Deletes a user if it exists.
     *
     * @param id UUID of the user to delete
     * @return {@code true} if the user existed and was deleted, {@code false} otherwise
     */
    public boolean delete(UUID id) {
        if (id == null || !repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    /**
     * Directly updates a user entity, mainly intended for testing scenarios.
     * <p>
     * If a non-blank password is present, it is encoded before persisting.
     * </p>
     *
     * @param user user to update
     * @return the saved {@link User}
     * @throws IllegalArgumentException if the user is null
     */
    public User update(User user) {
        if (user == null) throw new IllegalArgumentException("El usuario no puede ser null");

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return repository.save(user);
    }
}
