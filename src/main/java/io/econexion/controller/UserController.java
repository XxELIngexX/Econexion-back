package io.econexion.controller;

import io.econexion.model.User;
import io.econexion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios en memoria", description = "Gestión de usuarios para pruebas en memoria")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ✅ 1️⃣ Listar todos los usuarios
    @Operation(summary = "Listar todos los usuarios",
            description = "Devuelve todos los usuarios almacenados en memoria")
    @GetMapping
    public ResponseEntity<?> list() {
        List<User> salida = service.findAll();
        if (salida.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(salida);
    }

    // ✅ 2️⃣ Obtener usuario por ID
    @Operation(summary = "Obtener un usuario por ID",
            description = "Busca un usuario por su UUID y lo devuelve")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 3️⃣ Crear un nuevo usuario con validación @Valid
    @Operation(summary = "Crear un nuevo usuario",
            description = "Agrega un usuario en memoria y devuelve la información creada")
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.ok(service.create(user));
    }

    // ✅ 4️⃣ Actualizar usuario existente con validación @Valid
    @Operation(summary = "Actualizar un usuario",
            description = "Actualiza los datos de un usuario existente por UUID")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody User user) {
        return service.update(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 5️⃣ Eliminar usuario
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario por su UUID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ✅ 6️⃣ Consultar publicaciones de un usuario
    @Operation(summary = "Listar publicaciones del usuario",
            description = "Obtiene las publicaciones asociadas a un usuario")
    @GetMapping("/{id}/posts")
    public ResponseEntity<?> getUserPosts(@PathVariable UUID id) {
        return service.findById(id)
                .map(user -> ResponseEntity.ok(user.getPublications()))
                .orElse(ResponseEntity.notFound().build());
    }
}
