package io.econexion.controller;

import io.econexion.dtos.CreateOfferDTO;
import io.econexion.model.Offer;
import io.econexion.model.Post;
import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.OfferService;
import io.econexion.service.PostService;
import io.econexion.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    @Mock private OfferService offerService;
    @Mock private UserService userService;
    @Mock private PostService postService;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private OfferController controller;

    // --------------------------------------------------
    // CREATE OFFER - SUCCESS
    // --------------------------------------------------
    @Test
    void createOffer_success() {
        String jwt = "token";
        String email = "test@example.com";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);

        UUID pubId = UUID.randomUUID();
        Post post = new Post();
        post.setId(pubId);

        CreateOfferDTO dto = new CreateOfferDTO();
        dto.setAmount(100.0);
        dto.setMessage("Hola");
        dto.setPublicationId(pubId);

        Offer saved = new Offer();
        saved.setId(UUID.randomUUID());

        when(jwtUtil.extractUserName(jwt)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(postService.findById(pubId)).thenReturn(post);
        when(offerService.createOffer(any(Offer.class))).thenReturn(saved);

        ResponseEntity<?> r = controller.createOffer(dto, "Bearer " + jwt);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(saved, r.getBody());

        verify(jwtUtil).extractUserName(jwt);
        verify(userService).findByEmail(email);
        verify(postService).findById(pubId);
        verify(offerService).createOffer(any(Offer.class));
    }

    // --------------------------------------------------
    // CREATE OFFER - USER NOT FOUND
    // --------------------------------------------------
    @Test
    void createOffer_userNotFound() {
        String jwt = "token";
        String email = "noexiste@mail.com";

        when(jwtUtil.extractUserName(jwt)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        CreateOfferDTO dto = new CreateOfferDTO();
        dto.setAmount(20.0);
        dto.setPublicationId(UUID.randomUUID());

        assertThrows(RuntimeException.class, () ->
                controller.createOffer(dto, "Bearer " + jwt)
        );

        verify(jwtUtil).extractUserName(jwt);
        verify(userService).findByEmail(email);
        verifyNoInteractions(postService, offerService);
    }

    // --------------------------------------------------
    // CREATE OFFER - POST NOT FOUND
    // --------------------------------------------------
    @Test
    void createOffer_postNotFound() {
        String jwt = "token";
        String email = "test@mail.com";

        User u = new User();
        u.setId(UUID.randomUUID());
        u.setEmail(email);

        when(jwtUtil.extractUserName(jwt)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.of(u));

        // PostService retorna null si no encuentra el post
        when(postService.findById(any(UUID.class))).thenReturn(null);

        CreateOfferDTO dto = new CreateOfferDTO();
        dto.setAmount(50.0);
        dto.setPublicationId(UUID.randomUUID());

        assertThrows(RuntimeException.class, () ->
                controller.createOffer(dto, "Bearer " + jwt)
        );

        verify(postService).findById(any(UUID.class));
        verifyNoInteractions(offerService);
    }

    // --------------------------------------------------
    // GET OFFER BY ID
    // --------------------------------------------------
    @Test
    void getOfferById_found() {
        UUID id = UUID.randomUUID();
        Offer o = new Offer();
        o.setId(id);

        when(offerService.findById(id)).thenReturn(o);

        ResponseEntity<?> r = controller.getOfferById(id);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(o, r.getBody());
        verify(offerService).findById(id);
    }

    @Test
    void getOfferById_notFound() {
        UUID id = UUID.randomUUID();
        when(offerService.findById(id)).thenReturn(null);

        ResponseEntity<?> r = controller.getOfferById(id);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        verify(offerService).findById(id);
    }

    // --------------------------------------------------
    // DELETE OFFER
    // --------------------------------------------------
    @Test
    void deleteOffer_success() {
        UUID id = UUID.randomUUID();

        ResponseEntity<?> r = controller.deleteOffer(id);

        verify(offerService).deleteOffer(id);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("Oferta eliminada correctamente", r.getBody());
    }

    // --------------------------------------------------
    // UPDATE OFFER
    // --------------------------------------------------
    @Test
    void updateOffer_success() {
        Offer o = new Offer();
        o.setId(UUID.randomUUID());

        when(offerService.findById(o.getId())).thenReturn(o);
        when(offerService.createOffer(o)).thenReturn(o);

        ResponseEntity<?> r = controller.updateOffer(o);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(o, r.getBody());

        verify(offerService).findById(o.getId());
        verify(offerService).createOffer(o);
    }

    @Test
    void updateOffer_notFound() {
        Offer o = new Offer();
        o.setId(UUID.randomUUID());

        when(offerService.findById(o.getId())).thenReturn(null);

        ResponseEntity<?> r = controller.updateOffer(o);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        verify(offerService).findById(o.getId());
        verify(offerService, never()).createOffer(any());
    }
}
