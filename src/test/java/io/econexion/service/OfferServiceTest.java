package io.econexion.service;

import io.econexion.model.Offer;
import io.econexion.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
  @Mock private OfferRepository offerRepository;
  @InjectMocks private OfferService offerService;

  @Test
  void createOffer_persists() {
    Offer in = new Offer();
    in.setId(UUID.randomUUID());
    when(offerRepository.save(any(Offer.class))).thenReturn(in);
    Offer out = offerService.createOffer(in);
    assertEquals(in.getId(), out.getId());
    verify(offerRepository).save(in);
  }

  @Test
  void findById_whenPresent_returnsOffer() {
    UUID id = UUID.randomUUID();
    Offer o = new Offer(); o.setId(id);
    when(offerRepository.findById(id)).thenReturn(Optional.of(o));
    Offer got = offerService.findById(id);
    assertNotNull(got);
    assertEquals(id, got.getId());
  }
}