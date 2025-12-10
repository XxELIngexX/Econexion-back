package io.econexion.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.econexion.model.Post;

/**
 * Repository interface for performing CRUD operations on {@link Post} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Pagination and sorting capabilities</li>
 *   <li>Automatic query generation based on method names</li>
 * </ul>
 * Custom query methods can be added here if needed to support advanced filtering
 * or search functionality.
 * </p>
 */
@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

}
