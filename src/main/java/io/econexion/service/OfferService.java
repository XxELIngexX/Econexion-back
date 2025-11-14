package io.econexion.service;

import io.econexion.model.Offer;
import io.econexion.repository.OfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    // Obtener todas las ofertas
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    // Obtener por ID como Optional
    public Optional<Offer> getOfferById(UUID id) {
        return offerRepository.findById(id);
    }

    // 🔥 Nuevo método requerido por OfferController y OfferControllerTest
    public Offer findById(UUID id) {
        return offerRepository.findById(id).orElse(null);
    }

    // Crear nueva oferta
    public Offer createOffer(Offer offer) {
        return offerRepository.save(offer);
    }

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

    // Eliminar
    public boolean deleteOffer(UUID id) {
        if (offerRepository.existsById(id)) {
            offerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
