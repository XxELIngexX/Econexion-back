package io.econexion.dtos;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class CreateOfferDTO {
    private UUID publicationId;
    private double amount;
    private String message;
}
