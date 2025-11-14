package io.econexion.service;

import java.util.List;
import java.util.UUID;

import io.econexion.model.User;
import org.springframework.stereotype.Service;

import io.econexion.dtos.CreateOfferDTO;
import io.econexion.model.Offer;
import io.econexion.repository.OfferRepository;

@Service
public class OfferService {
    private OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Offer createOffer(Offer ofer) {
        return offerRepository.save(ofer);
    }

    public Offer findById(UUID id) {
        return offerRepository.findById(id).orElse(null);
    }

    public void deleteOffer(UUID id) {
        offerRepository.deleteById(id);
    }

    public List<Offer> findByPublicationOwner(User owner) {
        return offerRepository.findByPublicationOwner(owner);
    }

    public List<Offer> findByOfferer(User offerer) {
        return offerRepository.findByOfferer(offerer);
    }
}
