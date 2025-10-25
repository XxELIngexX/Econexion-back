package io.econexion.controller;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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




@RestController
@RequestMapping("/posts")
public class PostController {
    private PostService postservice;
    private UserService userservice;
    private JwtUtil jwtUtil;
    private ObjectMapper mapper;



    @Autowired
    public PostController(PostService postservice, UserService userservice, JwtUtil jwtUtil, ObjectMapper mapper) {
        this.postservice = postservice;
        this.userservice = userservice;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    @Operation(summary = "Crear un nuevo post",
            description = "Crea un nuevo post asociado al usuario autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post creado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado, token inválido o ausente"),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")})    
    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody CreatePostDto dto,
            @RequestHeader("Authorization") String userToken) throws Exception {


        String token = userToken.substring(7);
        String email = jwtUtil.extractUserName(token);
        User user = userservice.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setMaterial(dto.getMaterial());
        post.setQuantity(dto.getQuantity());
        post.setPrice(dto.getPrice());
        post.setLocation(dto.getLocation());
        post.setDescription(dto.getDescription());
        post.setOwner(user);

        postservice.savePost(post);

        user.getPublications().add(post);
        userservice.update(user);

        return ResponseEntity.ok().body(user);

    }

    @Operation(summary = "Obtener un post por ID",
            description = "Devuelve un post específico por su UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Post.class))),
                    @ApiResponse(responseCode = "404", description = "Post no encontrado")})
    @GetMapping()
    public ResponseEntity<?> getPostById(@RequestBody UUID id) throws NotFoundException {
            return ResponseEntity.ok().body(postservice.findById(id));
    }

    @Operation(summary = "Actualizar un post",
            description = "Actualiza un post existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post actualizado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Post.class))),
                    @ApiResponse(responseCode = "404", description = "Post no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")})
    @PutMapping("/update")
    public ResponseEntity<?> updatePost(@RequestBody Post post) {
        postservice.updatePost(post);
        return ResponseEntity.ok().body("Post actualizado");
    }

    @Operation(summary = "Eliminar un post",
            description = "Elimina un post existente por su UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Post no encontrado")})
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody UUID id) {
        postservice.deletePost(id);
        return ResponseEntity.ok().body("Post eliminado");
    }
}
