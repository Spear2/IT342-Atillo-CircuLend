package edu.cit.atillo.circulend.dto.transaction;

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