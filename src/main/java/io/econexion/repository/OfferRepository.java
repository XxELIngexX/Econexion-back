package io.econexion.repository;

import java.util.List;
import java.util.UUID;

import io.econexion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.econexion.model.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
    List<Offer> findByPublicationOwner(User owner);  // Nuevo (join publication.owner)
    List<Offer> findByOfferer(User offerer);
}
