package edu.cit.atillo.circulend.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserTransactionDTO {
    private Long transactionId;
    private Long userId;
    private Long itemId;
    private String itemName;
    private String assetTag;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;
}