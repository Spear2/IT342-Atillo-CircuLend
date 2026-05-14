package edu.cit.atillo.circulend.features.auditlogs.dto;

import edu.cit.atillo.circulend.entity.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuditLogResponseDTO {
    private Long auditId;
    private Long userId;
    private String userEmail;
    private String actionType;
    private String description;
    private LocalDateTime timestamp;

    public static AuditLogResponseDTO from(AuditLog log) {
        return new AuditLogResponseDTO(
                log.getAuditId(),
                log.getUser() != null ? log.getUser().getUserId() : null,
                log.getUser() != null ? log.getUser().getEmail() : null,
                log.getActionType() != null ? log.getActionType().name() : null,
                log.getDescription(),
                log.getTimestamp()
        );
    }
}
