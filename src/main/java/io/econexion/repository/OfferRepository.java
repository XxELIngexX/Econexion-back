package io.econexion.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.econexion.model.Offer;

/**
 * Repository interface for managing {@link Offer} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations
 * and query capabilities for offers, identified by a {@link UUID}.
 * Custom query methods can be added here as needed.
 * </p>
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

}
