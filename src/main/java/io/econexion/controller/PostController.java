package io.econexion.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.econexion.dtos.CreatePostDto;
import io.econexion.model.Post;
import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.PostService;
import io.econexion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

/**
 * REST controller responsible for managing posts (publications).
 * <p>
 * Provides endpoints for creating, retrieving, updating, and deleting posts.
 * Authenticated users can publish new posts, which are associated with their accounts.
 * </p>
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postservice;
    private final UserService userservice;
    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;

    /**
     * Constructs a new {@link PostController} with required dependencies.
     *
     * @param postservice service responsible for post operations
     * @param userservice service responsible for user operations
     * @param jwtUtil     utility for JWT extraction and validation
     * @param mapper      object mapper for JSON serialization (optional use)
     */
    @Autowired
    public PostController(PostService postservice, UserService userservice, JwtUtil jwtUtil, ObjectMapper mapper) {
        this.postservice = postservice;
        this.userservice = userservice;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    // ============================================================
    //                      CREATE POST
    // ============================================================

    /**
     * Creates a new post associated with the authenticated user.
     *
     * @param dto       DTO containing the post details
     * @param userToken Authorization header containing "Bearer <token>"
     * @return HTTP 200 with the updated {@link User} including the new post
     * @throws Exception if the user cannot be found or the JWT is invalid
     */
    @Operation(
            summary = "Crear un nuevo post",
            description = "Crea un nuevo post asociado al usuario autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post creado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado, token inválido o ausente"),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
            }
    )
    @PostMapping("/new")
    public ResponseEntity<?> create(
            @RequestBody CreatePostDto dto,
            @RequestHeader("Authorization") String userToken
    ) throws Exception {

        // Extract user from JWT
        String token = userToken.substring(7);
        String email = jwtUtil.extractUserName(token);

        User user = userservice.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Create Post entity
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setMaterial(dto.getMaterial());
        post.setQuantity(dto.getQuantity());
        post.setPrice(dto.getPrice());
        post.setLocation(dto.getLocation());
        post.setDescription(dto.getDescription());
        post.setOwner(user);

        // Persist post
        postservice.savePost(post);

        // Update user's publication list
        user.getPublications().add(post);
        userservice.update(user);

        return ResponseEntity.ok().body(user);
    }

    // ============================================================
    //                    GET POST BY ID
    // ============================================================

    /**
     * Retrieves a post by its UUID.
     *
     * @param id UUID of the post
     * @return HTTP 200 with the post, or 404 if not found
     * @throws NotFoundException if the post does not exist
     */
    @Operation(
            summary = "Obtener un post por ID",
            description = "Devuelve un post específico por su UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Post.class))),
                    @ApiResponse(responseCode = "404", description = "Post no encontrado")
            }
    )
    @GetMapping()
    public ResponseEntity<?> getPostById(@RequestBody UUID id) throws NotFoundException {
        return ResponseEntity.ok().body(postservice.findById(id));
    }

    // ============================================================
    //                    UPDATE POST
    // ============================================================

    /**
     * Updates an existing post.
     * <p>
     * The method does not validate ownership—this may be added depending on the application's rules.
     * </p>
     *
     * @param post the updated post entity
     * @return HTTP 200 with confirmation message
     */
    @Operation(
            summary = "Actualizar un post",
            description = "Actualiza un post existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post actualizado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Post.class))),
                    @ApiResponse(responseCode = "404", description = "Post no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
            }
    )
    @PutMapping("/update")
    public ResponseEntity<?> updatePost(@RequestBody Post post) {
        postservice.updatePost(post);
        return ResponseEntity.ok().body("Post actualizado");
    }

    // ============================================================
    //                    DELETE POST
    // ============================================================

    /**
     * Deletes an existing post by its UUID.
     *
     * @param id UUID of the post to delete
     * @return HTTP 200 with confirmation message
     */
    @Operation(
            summary = "Eliminar un post",
            description = "Elimina un post existente por su UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Post no encontrado")
            }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody UUID id) {
        postservice.deletePost(id);
        return ResponseEntity.ok().body("Post eliminado");
    }
}
