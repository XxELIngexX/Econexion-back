package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller that exposes CRUD operations and related endpoints
 * for managing {@link User} entities.
 * <p>
 * Base path: {@code /api/users}
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Gestión completa de usuarios")
public class UserController {

    /**
     * Service layer used to perform user-related operations.
     */
    private final UserService service;

    /**
     * Creates a new {@link UserController} with the required {@link UserService}.
     *
     * @param service the user service implementation
     */
    public UserController(UserService service) {
        this.service = service;
    }

    // ✅ 1️⃣ Listar todos los usuarios

    /**
     * Retrieves all users in the system.
     *
     * @return HTTP 200 with the list of users, or 404 if no users are found
     */
    @Operation(summary = "Listar todos los usuarios")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        List<User> salida = service.findAll();
        return salida.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(salida);
    }

    // ✅ 2️⃣ Obtener usuario por ID

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id UUID of the user to retrieve
     * @return HTTP 200 with the user if found, or 404 otherwise
     */
    @Operation(summary = "Obtener usuario por ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 3️⃣ Crear usuario

    /**
     * Creates a new user.
     *
     * @param user the user payload to create
     * @return HTTP 200 with the created {@link User}
     */
    @Operation(summary = "Crear nuevo usuario")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.ok(service.create(user));
    }

    // ✅ 4️⃣ Actualizar usuario

    /**
     * Updates an existing user identified by its UUID.
     *
     * @param id   UUID of the user to update
     * @param user payload containing the new user data
     * @return HTTP 200 with the updated user, or 404 if the user does not exist
     */
    @Operation(summary = "Actualizar usuario")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody User user) {
        return service.update(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 5️⃣ Eliminar usuario

    /**
     * Deletes a user identified by its UUID.
     *
     * @param id UUID of the user to delete
     * @return HTTP 204 (no content) if deleted, or 404 if the user does not exist
     */
    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ✅ 6️⃣ Publicaciones del usuario

    /**
     * Retrieves all posts (publications) associated with a given user.
     *
     * @param id UUID of the user whose posts are requested
     * @return HTTP 200 with the list of publications, or 404 if the user is not found
     */
    @Operation(summary = "Listar publicaciones del usuario")
    @GetMapping(value = "/{id}/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserPosts(@PathVariable UUID id) {
        return service.findById(id)
                .map(user -> ResponseEntity.ok(user.getPublications()))
                .orElse(ResponseEntity.notFound().build());
    }
}
