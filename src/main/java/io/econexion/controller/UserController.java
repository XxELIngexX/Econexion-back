package io.econexion.controller;

import io.econexion.dtos.CreateUserOAuth2Request;
import io.econexion.model.User;
import io.econexion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/lab/users")

@Tag(name = "Usuarios en memoria", description = "Gestión de usuarios para pruebas en memoria")
public class UserController {

        private final UserService service;

        public UserController(UserService service) {
                this.service = service;
        }

        /**
         * POST /lab/users/oauth2/create
         * Crea un usuario desde OAuth2 (Google).
         * Solo requiere email y nombre.
         */
        @PostMapping("/oauth2/create")
        public ResponseEntity<User> createOAuth2User(@RequestBody CreateUserOAuth2Request request) {
                try {
                        System.out.println("➕ Creando usuario OAuth2: " + request.getEmail() 
                        + ", " + request.getName()
                        + ", " + request.getRole()
                        );

                        // Crear usuario con valores por defecto
                        User newUser = new User();
                        newUser.setEmail(request.getEmail());
                        newUser.setUsername(request.getName());
                        newUser.setRole(request.getRole());

                        User createdUser = service.create(newUser);

                        System.out.println("✅ Usuario OAuth2 creado: ID=" + createdUser.getId());
                        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

                } catch (Exception e) {
                        System.err.println("❌ Error al crear usuario OAuth2: " + e.getMessage());

                        if (e.getMessage().contains("ya existe")) {
                                return ResponseEntity.status(HttpStatus.CONFLICT).build();
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        @Operation(summary = "Listar todos los usuarios", description = "Devuelve todos los usuarios almacenados en memoria", responses = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "si existen usuarios y retorna la lista de usuarios"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "no se encontraron registros de usuarios en el back")
        })

        @GetMapping("/allUsers")
        public ResponseEntity<?> list() {
                List<User> salida = service.findAll();
                if (salida.isEmpty()) {
                        return ResponseEntity.notFound().build();
                }

                return ResponseEntity.ok(salida);
        }

        @Operation(summary = "Obtener un usuario por ID", description = "Busca un usuario por su UUID y lo devuelve", responses = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado (consultar la documentacion del UserDto)"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @GetMapping("/getUser/{id}")
        public ResponseEntity<?> get(@PathVariable UUID id) throws Exception {
                return service.findById(id)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/get/{email}")
        public ResponseEntity<?> getByEmail(@PathVariable String email) throws Exception {
                return service.findByEmail(email)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @Operation(summary = "Crear un nuevo usuario", description = "Agrega un usuario en memoria y devuelve la información creada", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del usuario a crear", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class), examples = @ExampleObject(value = "{\"enterpriseName\": \"Corporation\", \"name\": \"Maria\", \"nit\": \"1236546987\", \"email\": \"maria@ejemplo.com\", \"password\": \"contraseñaSegura\",\"role\": \"seller/buyer\" }"))))
        @PostMapping("/addUser")
        public ResponseEntity<User> create(@RequestBody User user) throws Exception {
                return ResponseEntity.ok(service.create(user));
        }

        @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos de un usuario existente por UUID", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del usuario a actualizar", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class), examples = @ExampleObject(value = "{\"enterpriseName\": \"Corporation\", \"name\": \"Maria\", \"nit\": \"1236546987\", \"email\": \"maria@ejemplo.com\", \"password\": \"contraseñaSegura\",\"role\": \"seller/buyer\" }"))))
        @PutMapping("/update/{id}")
        public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody User user) throws Exception {
                return service.update(id, user)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario por su UUID")
        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable UUID id) throws Exception {
                return service.delete(id) ? ResponseEntity.noContent().build()
                                : ResponseEntity.notFound().build();
        }

        // @GetMapping("/post")
        // public ResponseEntity<?> getUserPost(UUID id) throws Exception {
        //         User user = service.findById(id).orElseThrow(() -> new Exception("Usuario no encontrado"));
        //         return ResponseEntity.ok().body(user.getPublications());
        // }

        @GetMapping("/exists/{email}")
        public ResponseEntity<?> existsByEmail(@PathVariable String email) {
                User exists = service.findByEmail(email).orElse(null);
                if (exists == null) {
                        return ResponseEntity.ok().body(false);
                }
                return ResponseEntity.ok().body(exists);
        }
}
