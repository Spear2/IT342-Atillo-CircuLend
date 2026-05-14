package edu.cit.atillo.circulend.features.auditlogs;

import edu.cit.atillo.circulend.features.auditlogs.dto.AuditLogResponseDTO;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public List<AuditLogResponseDTO> listLogs(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 200);
        return auditLogRepository.findAll(PageRequest.of(safePage, safeSize, Sort.by("timestamp").descending()))
                .map(AuditLogResponseDTO::from)
                .getContent();
    }
}
