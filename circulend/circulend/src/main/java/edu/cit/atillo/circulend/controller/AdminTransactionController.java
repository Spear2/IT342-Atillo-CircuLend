package edu.cit.atillo.circulend.controller;

import edu.cit.atillo.circulend.dto.ApiResponse;
import edu.cit.atillo.circulend.dto.transaction.UserTransactionDTO;
import edu.cit.atillo.circulend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserTransactionDTO>>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        List<UserTransactionDTO> all = transactionService.getAllTransactions();
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        List<UserTransactionDTO> slice = all.subList(from, to);

        return ResponseEntity.ok(ApiResponse.success(slice));
    }
}