package io.econexion.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.econexion.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

}
