package io.econexion.dtos;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object used to create a new offer for a given publication.
 * <p>
 * This DTO carries the identifier of the target publication, the offered amount,
 * and an optional message from the user making the offer.
 * </p>
 */
@Getter
@Setter
public class CreateOfferDTO {

    /**
     * Unique identifier of the publication (post) to which this offer belongs.
     */
    private UUID publicationId;

    /**
     * Monetary amount offered for the publication.
     */
    private double amount;

    /**
     * Optional message or note associated with the offer.
     */
    private String message;
}
