package io.econexion.service;

import java.util.UUID;

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


    
}
