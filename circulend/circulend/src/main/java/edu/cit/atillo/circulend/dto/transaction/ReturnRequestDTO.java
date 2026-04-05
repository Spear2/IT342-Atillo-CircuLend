package edu.cit.atillo.circulend.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReturnRequestDTO {
    @NotBlank
    private String assetTag;
}