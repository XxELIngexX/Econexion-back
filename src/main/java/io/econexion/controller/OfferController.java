package io.econexion.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.econexion.dtos.CreateOfferDTO;
import io.econexion.model.Offer;
import io.econexion.model.Post;
import io.econexion.model.User;
import io.econexion.security.JwtUtil;
import io.econexion.service.OfferService;
import io.econexion.service.PostService;
import io.econexion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/offers")
public class OfferController {
    private OfferService offerService;
    private UserService userservice;
    private JwtUtil jwtUtil;
    private PostService postService;

    public OfferController(OfferService offerService, JwtUtil jwtUtil, PostService postService,
            UserService userservice) {
        this.offerService = offerService;
        this.jwtUtil = jwtUtil;
        this.postService = postService;
        this.userservice = userservice;
    }
    
    @Operation (summary = "Crear una nueva oferta",
            description = "Crea una nueva oferta asociada al usuario autenticado y a una publicación específica",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oferta creada exitosamente"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario o publicación no encontrado")
            })

    @PostMapping("/new")
    public ResponseEntity<?> createOffer(@RequestBody CreateOfferDTO dto,
            @RequestHeader("Authorization") String userToken) throws Exception {
        String token = userToken.substring(7);
        String email = jwtUtil.extractUserName(token);
        User user = userservice.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        Post post = postService.findById(dto.getPublicationId());

        Offer offer = new Offer();
        offer.setAmount(dto.getAmount());
        offer.setMessage(dto.getMessage());
        offer.setOfferer(user);
        offer.setPublication(post);
        return ResponseEntity.ok(offerService.createOffer(offer));
    }

    @Operation(summary = "Obtener una oferta por ID",
            description = "Recupera los detalles de una oferta específica utilizando su ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oferta encontrada exitosamente"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Oferta no encontrada")
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOfferById(@PathVariable UUID id) {
        Offer offer = offerService.findById(id);
        if (offer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(offer);
    }

    @Operation(summary = "Eliminar una oferta por ID",
            description = "Elimina una oferta específica utilizando su ID")

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable UUID id) {
        offerService.deleteOffer(id);
        return ResponseEntity.ok("Oferta eliminada correctamente");
    }

    @Operation(summary = "Actualizar una oferta",
            description = "Actualiza los detalles de una oferta existente",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oferta actualizada exitosamente"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Oferta no encontrada")
            })
    @PutMapping("/update")
    public ResponseEntity<?> updateOffer(@RequestBody Offer offer) {
        if (offerService.findById(offer.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(offerService.createOffer(offer));
    }

    @GetMapping("/received")  // Nuevo: Offers received (on my posts)
    @Operation(summary = "Obtener offers recibidas", description = "Offers en posts del user")
    public ResponseEntity<List<Offer>> getReceivedOffers(@RequestHeader("Authorization") String userToken) throws Exception {
        String token = userToken.substring(7);
        String email = jwtUtil.extractUserName(token);
        User user = userservice.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        return ResponseEntity.ok(offerService.findByPublicationOwner(user));
    }

    @GetMapping("/sent")  // Nuevo: Offers sent by me
    @Operation(summary = "Obtener offers enviadas", description = "Offers hechas por el user")
    public ResponseEntity<List<Offer>> getSentOffers(@RequestHeader("Authorization") String userToken) throws Exception {
        String token = userToken.substring(7);
        String email = jwtUtil.extractUserName(token);
        User user = userservice.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        return ResponseEntity.ok(offerService.findByOfferer(user));
    }
}
