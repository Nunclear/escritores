package com.nunclear.escritores.dto.response;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String actionName,
        String tableName,
        String entityName,
        String recordId,
        Integer userId,
        String username,
        String requestMethod,
        String requestPath,
        String description,
        LocalDateTime createdAt
) {
}
