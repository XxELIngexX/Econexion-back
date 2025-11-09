package io.econexion.model;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    // --- Campos “nuevos” ---
    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    // --- Compatibilidad con controladores antiguos ---
    @NotBlank(message = "Material cannot be blank")
    @Column(name = "material", nullable = false)
    private String material;

    @NotNull(message = "Quantity cannot be null")
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @NotNull(message = "Price cannot be null")
    @Column(name = "price", nullable = false)
    private Double price;

    @NotBlank(message = "Location cannot be blank")
    @Column(name = "location", nullable = false)
    private String location;

    @NotBlank(message = "Description cannot be blank")
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    // Relación con el propietario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-posts")
    private User owner;

    // Relación con las ofertas asociadas
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Offer> offers = new ArrayList<>();

    public Post() {}

    public Post(String title, String content, User owner) {
        this.title = title;
        this.content = content;
        this.material = title;
        this.description = content;
        this.owner = owner;
    }

    public Post(String material, Double quantity, Double price, String location, String description, User owner) {
        this.material = material;
        this.quantity = quantity;
        this.price = price;
        this.location = location;
        this.description = description;
        this.title = material;
        this.content = description;
        this.owner = owner;
    }
}
