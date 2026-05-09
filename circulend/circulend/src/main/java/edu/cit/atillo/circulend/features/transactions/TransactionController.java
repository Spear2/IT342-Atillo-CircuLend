package edu.cit.atillo.circulend.features.transactions;

import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import edu.cit.atillo.circulend.features.transactions.dto.BorrowRequestDTO;
import edu.cit.atillo.circulend.features.transactions.dto.BorrowResponseDTO;
import edu.cit.atillo.circulend.features.transactions.dto.ReturnRequestDTO;
import edu.cit.atillo.circulend.features.transactions.dto.ReturnResponseDTO;
import edu.cit.atillo.circulend.features.transactions.dto.UserTransactionDTO;
import edu.cit.atillo.circulend.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUserService currentUserService;

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowResponseDTO>> borrow(@Valid @RequestBody BorrowRequestDTO request) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(transactionService.borrow(request, userId)));
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<ApiResponse<ReturnResponseDTO>> returnItem(
            @PathVariable Long id,
            @Valid @RequestBody ReturnRequestDTO request
    ) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(transactionService.returnItem(id, request, userId)));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<UserTransactionDTO>>> getUserTransactions() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(transactionService.getUserTransactions(userId)));
    }
}
