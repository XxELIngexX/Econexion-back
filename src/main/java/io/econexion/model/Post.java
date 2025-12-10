package io.econexion.model;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity representing a publication (post) created by a user.
 * <p>
 * This model supports both a newer structure ({@code title}, {@code content})
 * and legacy compatibility fields ({@code material}, {@code quantity},
 * {@code price}, {@code location}, {@code description}) used by older controllers.
 * Convenience constructors keep both sets of fields in sync.
 * </p>
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "posts")
public class Post {

    /**
     * Primary key identifier of the post.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    // --- New fields ---

    /**
     * Title of the post.
     */
    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Main textual content of the post.
     */
    @NotBlank(message = "Content cannot be blank")
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    // --- Compatibility with legacy controllers ---

    /**
     * Material associated with the post (legacy field).
     * <p>
     * Usually mirrors {@link #title}.
     * </p>
     */
    @NotBlank(message = "Material cannot be blank")
    @Column(name = "material", nullable = false)
    private String material;

    /**
     * Quantity of the material (legacy field).
     */
    @NotNull(message = "Quantity cannot be null")
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    /**
     * Price associated with the material or post (legacy field).
     */
    @NotNull(message = "Price cannot be null")
    @Column(name = "price", nullable = false)
    private Double price;

    /**
     * Location where the material or offer is available (legacy field).
     */
    @NotBlank(message = "Location cannot be blank")
    @Column(name = "location", nullable = false)
    private String location;

    /**
     * Detailed description of the post or material (legacy field).
     * <p>
     * Usually mirrors {@link #content}.
     * </p>
     */
    @NotBlank(message = "Description cannot be blank")
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    /**
     * Owner (author) of the post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-posts")
    private User owner;

    /**
     * Offers associated with this post.
     * <p>
     * When a post is removed, its offers are also cascaded.
     * </p>
     */
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Offer> offers = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public Post() {}

    /**
     * Convenience constructor using the newer structure ({@code title}, {@code content}).
     * <p>
     * Legacy fields {@code material} and {@code description} are populated accordingly.
     * </p>
     *
     * @param title   title of the post
     * @param content main content of the post
     * @param owner   owner (author) of the post
     */
    public Post(String title, String content, User owner) {
        this.title = title;
        this.content = content;
        this.material = title;
        this.description = content;
        this.owner = owner;
    }

    /**
     * Convenience constructor using the legacy structure
     * ({@code material}, {@code quantity}, {@code price}, {@code location}, {@code description}).
     * <p>
     * Newer fields {@code title} and {@code content} are populated from legacy values.
     * </p>
     *
     * @param material    material name
     * @param quantity    quantity of material
     * @param price       price of the post/material
     * @param location    location where it is available
     * @param description detailed description
     * @param owner       owner (author) of the post
     */
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
