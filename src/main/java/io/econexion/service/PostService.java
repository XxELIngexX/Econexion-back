package io.econexion.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.econexion.model.Post;
import io.econexion.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postrepository;

    // ===============================
    //       GUARDAR POST
    // ===============================
    public Post savePost(Post post) { 
        return postrepository.save(post);
    }

    // ===============================
    //       BUSCAR POR ID
    // ===============================
    // ❗ IMPORTANTE: SIN throws NotFoundException
    public Post findById(UUID id) {
        return postrepository.findById(id).orElse(null);
    }

    // ===============================
    //         ELIMINAR POST
    // ===============================
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
    public ResponseEntity<?> updatePost(Post post) {

        if (!postrepository.findById(post.getId()).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(postrepository.save(post));
    }
}
