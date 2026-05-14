package edu.cit.atillo.circulend.features.transactions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRequestDTO {
    @NotNull
    private Long itemId;

    @NotBlank
    private String assetTag;
}
