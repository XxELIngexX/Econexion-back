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
  @Mock private PostRepository postrepository;
  @InjectMocks private PostService postService;

  @Test
  void updatePost_whenNotFound_returns404() {
    Post p = new Post(); p.setId(UUID.randomUUID());
    when(postrepository.findById(p.getId())).thenReturn(Optional.empty());
    ResponseEntity<?> resp = postService.updatePost(p);
    assertEquals(404, resp.getStatusCode().value());
    verify(postrepository, never()).save(any());
  }

  @Test
  void updatePost_whenExists_updatesAndReturns200() {
    Post p = new Post(); p.setId(UUID.randomUUID());
    when(postrepository.findById(p.getId())).thenReturn(Optional.of(p));
    when(postrepository.save(p)).thenReturn(p);
    ResponseEntity<?> resp = postService.updatePost(p);
    assertEquals(200, resp.getStatusCode().value());
    assertTrue(resp.getBody() instanceof Post);
    verify(postrepository).save(p);
  }
}