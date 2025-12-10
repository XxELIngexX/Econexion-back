package io.econexion.dtos;

import lombok.Data;

/**
 * Data Transfer Object used to create a new post (publication).
 * <p>
 * This DTO contains all the information required to publish a new material listing,
 * including title, material type, quantity, price, location, and description.
 * </p>
 */
@Data
public class CreatePostDto {

    /**
     * Title of the post or publication.
     */
    private String title;

    /**
     * Type of material being published.
     */
    private String material;

    /**
     * Quantity of the material available.
     */
    private double quantity;

    /**
     * Price assigned to the material or item being published.
     */
    private double price;

    /**
     * Physical or geographical location associated with the publication.
     */
    private String location;

    /**
     * Detailed description of the publication or material.
     */
    private String description;
}
