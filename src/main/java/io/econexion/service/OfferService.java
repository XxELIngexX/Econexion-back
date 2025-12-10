package io.econexion.service;

import io.econexion.model.Offer;
import io.econexion.repository.OfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing {@link Offer} entities.
 * <p>
 * This service encapsulates business logic related to offers and
 * delegates persistence operations to {@link OfferRepository}.
 * It exposes CRUD-style methods used by controllers.
 * </p>
 */
@Service
public class OfferService {

    /**
     * Repository used to access and persist {@link Offer} data.
     */
    private final OfferRepository offerRepository;

    /**
     * Constructs a new {@link OfferService} with the required repository.
     *
     * @param offerRepository repository responsible for {@link Offer} persistence
     */
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    /**
     * Retrieves all offers stored in the system.
     *
     * @return a list of all {@link Offer} entities
     */
    // Obtener todas las ofertas
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    /**
     * Retrieves an offer by its unique identifier as an {@link Optional}.
     *
     * @param id UUID of the offer to look up
     * @return an {@link Optional} containing the offer if found, or empty otherwise
     */
    // Obtener por ID como Optional
    public Optional<Offer> getOfferById(UUID id) {
        return offerRepository.findById(id);
    }

    /**
     * Retrieves an offer by its unique identifier, returning {@code null}
     * if no entity is found.
     * <p>
     * This method exists for compatibility with controllers and tests
     * that expect a nullable result instead of an {@link Optional}.
     * </p>
     *
     * @param id UUID of the offer to look up
     * @return the {@link Offer} if found, or {@code null} otherwise
     */
    // 🔥 Nuevo método requerido por OfferController y OfferControllerTest
    public Offer findById(UUID id) {
        return offerRepository.findById(id).orElse(null);
    }

    /**
     * Creates and persists a new offer.
     *
     * @param offer the offer entity to persist
     * @return the saved {@link Offer} with generated identifiers
     */
    // Crear nueva oferta
    public Offer createOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    /**
     * Updates an existing offer with new data.
     * <p>
     * Only specific fields are updated:
     * amount, message, publication and offerer. If the offer does not exist,
     * an empty {@link Optional} is returned.
     * </p>
     *
     * @param id      UUID of the offer to update
     * @param newData object containing the new data for the offer
     * @return an {@link Optional} with the updated {@link Offer}, or empty if not found
     */
    // Actualizar oferta existente
    public Optional<Offer> updateOffer(UUID id, Offer newData) {
        return offerRepository.findById(id).map(existing -> {
            existing.setAmount(newData.getAmount());
            existing.setMessage(newData.getMessage());
            existing.setPublication(newData.getPublication());
            existing.setOfferer(newData.getOfferer());
            return offerRepository.save(existing);
        });
    }

    /**
     * Deletes an offer identified by its UUID, if it exists.
     *
     * @param id UUID of the offer to delete
     * @return {@code true} if the offer existed and was deleted, {@code false} otherwise
     */
    // Eliminar
    public boolean deleteOffer(UUID id) {
        if (offerRepository.existsById(id)) {
            offerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
