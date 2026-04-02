package edu.cit.atillo.circulend.repository;

import edu.cit.atillo.circulend.entity.AuditLog;
import edu.cit.atillo.circulend.entity.enums.AuditActionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserUserIdOrderByTimestampDesc(Long userId);
    List<AuditLog> findByActionTypeOrderByTimestampDesc(AuditActionType actionType);
}