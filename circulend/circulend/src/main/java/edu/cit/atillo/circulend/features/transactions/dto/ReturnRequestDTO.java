package edu.cit.atillo.circulend.features.transactions.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReturnRequestDTO {
    @NotBlank
    private String assetTag;
}
