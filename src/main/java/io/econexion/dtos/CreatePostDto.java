package io.econexion.dtos;

import lombok.Data;

@Data
public class CreatePostDto {
    private String title;
    private String material;
    private double quantity;
    private double price;
    private String location;
    private String description;

}
