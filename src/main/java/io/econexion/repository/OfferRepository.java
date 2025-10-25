package io.econexion.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.econexion.model.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

    
}
