package edu.cit.atillo.circulend.controller;

import edu.cit.atillo.circulend.dto.ApiResponse;
import edu.cit.atillo.circulend.dto.transaction.*;
import edu.cit.atillo.circulend.security.CurrentUserService;
import edu.cit.atillo.circulend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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