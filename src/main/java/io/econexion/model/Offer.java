package io.econexion.model;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    // --- Campos “nuevos” ---
    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Price cannot be null")
    @Column(name = "price", nullable = false)
    private Double price;

    // --- Compatibilidad con controladores antiguos ---
    @NotNull(message = "Amount cannot be null")
    @Column(name = "amount", nullable = false)
    private Double amount;

    @NotBlank(message = "Message cannot be blank")
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    // Relación con el usuario que ofrece
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-offers")
    private User offerer;

    // Relación con la publicación asociada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post publication;

    public Offer() {}

    public Offer(String title, String description, Double price, User offerer, Post publication) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.amount = price;
        this.message = description;
        this.offerer = offerer;
        this.publication = publication;
    }

    public Offer(Double amount, String message, User offerer, Post publication) {
        this.amount = amount;
        this.message = message;
        this.price = amount;
        this.description = message;
        this.title = "Offer";
        this.offerer = offerer;
        this.publication = publication;
    }
}
