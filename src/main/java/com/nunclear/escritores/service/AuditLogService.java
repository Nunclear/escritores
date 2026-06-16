package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.response.AuditLogResponse;
import com.nunclear.escritores.dto.response.PageResponse;
import com.nunclear.escritores.entity.AuditLog;
import com.nunclear.escritores.repository.AuditLogRepository;
import com.nunclear.escritores.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public PageResponse<AuditLogResponse> listAuditLogs(
            String actionName,
            String tableName,
            Integer userId,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = PaginationUtils.buildPageable(page, size, sort == null || sort.isBlank() ? "createdAt,desc" : sort, field -> field);
        Page<AuditLog> result = auditLogRepository.searchAuditLogs(
                normalize(actionName),
                normalize(tableName),
                userId,
                pageable
        );

        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getActionName(),
                log.getTableName(),
                log.getEntityName(),
                log.getRecordId(),
                log.getUserId(),
                log.getUsername(),
                log.getRequestMethod(),
                log.getRequestPath(),
                log.getDescription(),
                log.getCreatedAt()
        );
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
