package io.econexion.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.econexion.model.Post;
import io.econexion.repository.PostRepository;

/**
 * Service layer for managing {@link Post} entities.
 * <p>
 * Encapsulates business logic for creating, retrieving, updating and deleting
 * posts, delegating persistence operations to {@link PostRepository}.
 * </p>
 */
@Service
public class PostService {

    @Autowired
    private PostRepository postrepository;

    // ===============================
    //       GUARDAR POST
    // ===============================

    /**
     * Persists a post in the underlying repository.
     *
     * @param post the {@link Post} entity to be saved
     * @return the persisted {@link Post}, including any generated fields
     */
    public Post savePost(Post post) { 
        return postrepository.save(post);
    }

    // ===============================
    //       BUSCAR POR ID
    // ===============================
    // ❗ IMPORTANTE: SIN throws NotFoundException

    /**
     * Retrieves a post by its unique identifier.
     * <p>
     * If the post does not exist, this method returns {@code null}
     * instead of throwing an exception.
     * </p>
     *
     * @param id the UUID of the post to find
     * @return the {@link Post} if found, or {@code null} otherwise
     */
    public Post findById(UUID id) {
        return postrepository.findById(id).orElse(null);
    }

    // ===============================
    //         ELIMINAR POST
    // ===============================

    /**
     * Deletes a post by its unique identifier.
     * <p>
     * If the post is not found, a 404 (Not Found) response is returned.
     * Otherwise, the post is deleted and a 200 (OK) response with a message
     * is returned.
     * </p>
     *
     * @param id the UUID of the post to delete
     * @return a {@link ResponseEntity} indicating the result of the operation
     */
    public ResponseEntity<?> deletePost(UUID id) {

        if (!postrepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        postrepository.deleteById(id);
        return ResponseEntity.ok("post eliminado correctamente");
    }

    // ===============================
    //        ACTUALIZAR POST
    // ===============================

    /**
     * Updates an existing post.
     * <p>
     * If the post does not exist, a 404 (Not Found) response is returned.
     * Otherwise, the post is updated and the persisted entity is returned
     * with a 200 (OK) response.
     * </p>
     *
     * @param post the {@link Post} entity containing updated data
     * @return a {@link ResponseEntity} with the updated post or 404 if not found
     */
    public ResponseEntity<?> updatePost(Post post) {

        if (!postrepository.findById(post.getId()).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(postrepository.save(post));
    }
}
