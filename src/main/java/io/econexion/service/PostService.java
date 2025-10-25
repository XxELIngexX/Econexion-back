package io.econexion.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.econexion.model.Post;
import io.econexion.model.User;
import io.econexion.repository.PostRepository;

@Service
public class PostService {
    @Autowired
    PostRepository postrepository;
    @Autowired
    UserService userservice;
    
    public Post savePost(Post post){ 
        return postrepository.save(post);
    }

    public Post findById(UUID id) throws NotFoundException {
        return postrepository.findById(id).orElseThrow(() -> new NotFoundException());    
    }
   
    public ResponseEntity<?> deletePost(UUID id){
        if(!postrepository.findById(id).isPresent()){
            return ResponseEntity.notFound().build();
        }
        postrepository.deleteById(id);
        return ResponseEntity.ok("post eliminado, correctamente");
    }
   
    public ResponseEntity<?> updatePost(Post post){

         if(!postrepository.findById(post.getId()).isPresent()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(postrepository.save(post));

    }
    

}
