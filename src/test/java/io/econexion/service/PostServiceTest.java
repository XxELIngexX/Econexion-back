package io.econexion.service;

import io.econexion.model.Post;
import io.econexion.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void updatePost_whenNotFound_returns404() {
        Post p = new Post();
        p.setId(UUID.randomUUID());

        when(postRepository.findById(p.getId())).thenReturn(Optional.empty());

        ResponseEntity<?> resp = postService.updatePost(p);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    void updatePost_whenExists_updatesAndReturns200() {
        Post p = new Post();
        p.setId(UUID.randomUUID());

        when(postRepository.findById(p.getId())).thenReturn(Optional.of(p));
        when(postRepository.save(p)).thenReturn(p);

        ResponseEntity<?> resp = postService.updatePost(p);
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody() instanceof Post);
    }

    @Test
    void deletePost_ok() {
        UUID id = UUID.randomUUID();
        Post mockPost = new Post();
        mockPost.setId(id);

        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));
        doNothing().when(postRepository).deleteById(id);

        ResponseEntity<?> resp = postService.deletePost(id);

        assertEquals(200, resp.getStatusCode().value());
        verify(postRepository).deleteById(id);
    }
}
