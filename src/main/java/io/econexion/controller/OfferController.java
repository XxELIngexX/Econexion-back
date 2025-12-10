package io.econexion.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.econexion.dtos.CreateOfferDTO;
import io.econexion.model.Offer;
import io.econexion.model.Post;
import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.OfferService;
import io.econexion.service.PostService;
import io.econexion.service.UserService;

/**
 * REST controller responsible for managing offer-related operations,
 * such as creation, retrieval, updating, and deletion.
 * <p>
 * This controller integrates with JWT authentication to validate
 * the requesting user and interacts with offer, post, and user services
 * for business logic execution.
 * </p>
 */
@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PostService postService;

    /**
     * Constructs a new {@link OfferController} with required dependencies.
     *
     * @param offerService service handling offer business logic
     * @param jwtUtil      utility class for extracting JWT information
     * @param postService  service handling publication/post operations
     * @param userService  service handling user operations
     */
    public OfferController(OfferService offerService, JwtUtil jwtUtil,
                           PostService postService, UserService userService) {
        this.offerService = offerService;
        this.jwtUtil = jwtUtil;
        this.postService = postService;
        this.userService = userService;
    }

    // ===============================
    //           CREATE OFFER
    // ===============================

    /**
     * Creates a new offer under a given publication.
     * <p>
     * The method extracts the authenticated user's email from the JWT,
     * validates their existence, verifies the referenced post, and then
     * creates an offer associated with that post and user.
     * </p>
     *
     * @param dto       DTO containing offer details
     * @param userToken JWT token provided in the Authorization header
     * @return the created {@link Offer}, wrapped in a {@link ResponseEntity}
     */
    @PostMapping("/new")
    public ResponseEntity<?> createOffer(
            @RequestBody CreateOfferDTO dto,
            @RequestHeader("Authorization") String userToken
    ) {

        // 1. Extract email from JWT
        String token = userToken.substring(7);
        String email = jwtUtil.extractUserName(token);

        // 2. Validate user
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // 3. Validate publication
        Post post = postService.findById(dto.getPublicationId());
        if (post == null) {
            throw new RuntimeException("Publicación no encontrada");
        }

        // 4. Create offer entity
        Offer offer = new Offer();
        offer.setAmount(dto.getAmount());
        offer.setMessage(dto.getMessage());
        offer.setOfferer(user);
        offer.setPublication(post);

        return ResponseEntity.ok(offerService.createOffer(offer));
    }

    // ===============================
    //           GET BY ID
    // ===============================

    /**
     * Retrieves an offer by its unique identifier.
     *
     * @param id offer UUID
     * @return HTTP 200 with the offer, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOfferById(@PathVariable UUID id) {
        Offer offer = offerService.findById(id);

        if (offer == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(offer);
    }

    // ===============================
    //           DELETE OFFER
    // ===============================

    /**
     * Deletes an offer by ID.
     * <p>
     * The project's tests expect HTTP 200 in all cases,
     * regardless of whether the ID existed or not.
     * </p>
     *
     * @param id offer UUID
     * @return HTTP 200 with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable UUID id) {

        offerService.deleteOffer(id); // tests expect this to always respond 200

        return ResponseEntity.ok("Oferta eliminada correctamente");
    }

    // ===============================
    //           UPDATE OFFER
    // ===============================

    /**
     * Updates an existing offer.
     * <p>
     * If the offer does not exist, a 404 response is returned.
     * Otherwise, the offer is updated and returned.
     * </p>
     *
     * @param offer updated offer object
     * @return HTTP 200 with updated offer, or 404 if not found
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateOffer(@RequestBody Offer offer) {

        Offer existing = offerService.findById(offer.getId());

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        Offer updated = offerService.createOffer(offer);

        return ResponseEntity.ok(updated);
    }
}
