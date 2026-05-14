package edu.cit.atillo.circulend.features.auditlogs;

import edu.cit.atillo.circulend.entity.AuditLog;
import edu.cit.atillo.circulend.entity.enums.AuditActionType;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void listLogsReturnsMappedDtos() {
        AuditLog log = new AuditLog();
        log.setAuditId(1L);
        log.setActionType(AuditActionType.SMTP_SENT);
        log.setDescription("sent");
        log.setTimestamp(LocalDateTime.now());
        Page<AuditLog> page = new PageImpl<>(List.of(log));

        when(auditLogRepository.findAll(any(Pageable.class))).thenReturn(page);

        var result = auditLogService.listLogs(0, 20);
        assertEquals(1, result.size());
        assertEquals("SMTP_SENT", result.get(0).getActionType());
    }
}
