package edu.cit.atillo.circulend.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BorrowResponseDTO {
    private Long transactionId;
    private Long itemId;
    private String itemName;
    private LocalDateTime borrowDate;
    private String status;
}