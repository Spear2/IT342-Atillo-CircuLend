package edu.cit.atillo.circulend.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReturnResponseDTO {
    private Long transactionId;
    private LocalDateTime returnDate;
    private String status;
}