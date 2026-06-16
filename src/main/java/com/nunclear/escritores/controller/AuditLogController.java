package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.response.AuditLogResponse;
import com.nunclear.escritores.dto.response.PageResponse;
import com.nunclear.escritores.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auditoria")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<AuditLogResponse> listAuditLogs(
            @RequestParam(required = false) String actionName,
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        return auditLogService.listAuditLogs(actionName, tableName, userId, page, size, sort);
    }
}
