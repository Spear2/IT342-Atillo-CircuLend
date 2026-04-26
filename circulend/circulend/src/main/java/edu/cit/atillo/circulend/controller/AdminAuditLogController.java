package edu.cit.atillo.circulend.controller;


import edu.cit.atillo.circulend.dto.ApiResponse;
import edu.cit.atillo.circulend.dto.AuditLogResponseDTO;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/auditlog")
@RequiredArgsConstructor
public class AdminAuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponseDTO>>> listLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 200);

        var result = auditLogRepository.findAll(
                org.springframework.data.domain.PageRequest.of(
                        safePage, safeSize,
                        org.springframework.data.domain.Sort.by("timestamp").descending()
                )
        ).map(AuditLogResponseDTO::from);

        return ResponseEntity.ok(ApiResponse.success(result.getContent()));
    }
}
