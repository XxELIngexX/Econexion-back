package io.econexion.repository;

import java.util.List;
import java.util.UUID;

import io.econexion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.econexion.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByOwner(User owner);
    List<Post> findAll();
}
