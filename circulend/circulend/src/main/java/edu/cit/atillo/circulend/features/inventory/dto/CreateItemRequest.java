package edu.cit.atillo.circulend.features.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItemRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long categoryId;

    @NotBlank
    private String assetTag;
}
