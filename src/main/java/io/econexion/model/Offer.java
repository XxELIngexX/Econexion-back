package io.econexion.model;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity representing an offer made by a user on a specific {@link Post}.
 * <p>
 * This model supports both the new offer structure (title, description, price)
 * and legacy compatibility fields (amount, message) used by older controllers.
 * The legacy fields are kept in sync with the new ones through convenience constructors.
 * </p>
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "offers")
public class Offer {

    /**
     * Primary key identifier of the offer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Title of the offer.
     */
    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Detailed description of the offer.
     */
    @NotBlank(message = "Description cannot be blank")
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    /**
     * Price proposed in this offer.
     */
    @NotNull(message = "Price cannot be null")
    @Column(name = "price", nullable = false)
    private Double price;

    /**
     * Legacy field representing the monetary amount of the offer.
     * <p>
     * Kept for backward compatibility with older controllers and APIs.
     * Typically mirrors {@link #price}.
     * </p>
     */
    @NotNull(message = "Amount cannot be null")
    @Column(name = "amount", nullable = false)
    private Double amount;

    /**
     * Legacy field representing the message or note attached to the offer.
     * <p>
     * Kept for backward compatibility with older controllers and APIs.
     * Typically mirrors {@link #description}.
     * </p>
     */
    @NotBlank(message = "Message cannot be blank")
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    /**
     * User who created/made this offer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-offers")
    private User offerer;

    /**
     * Publication (post) to which this offer is associated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post publication;

    /**
     * Default constructor required by JPA.
     */
    public Offer() {}

    /**
     * Convenience constructor for creating an offer using the newer structure
     * (title, description, price). Legacy fields {@code amount} and
     * {@code message} are automatically derived from these values.
     *
     * @param title       the title of the offer
     * @param description the description of the offer
     * @param price       the price proposed in the offer
     * @param offerer     the user who makes the offer
     * @param publication the related post/publication
     */
    public Offer(String title, String description, Double price, User offerer, Post publication) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.amount = price;
        this.message = description;
        this.offerer = offerer;
        this.publication = publication;
    }

    /**
     * Convenience constructor for creating an offer using the legacy fields
     * ({@code amount}, {@code message}). New fields are populated to keep
     * the data model consistent.
     *
     * @param amount      the monetary amount of the offer
     * @param message     the message or note attached to the offer
     * @param offerer     the user who makes the offer
     * @param publication the related post/publication
     */
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
