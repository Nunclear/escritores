package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
            SELECT a
            FROM AuditLog a
            WHERE (:actionName IS NULL OR LOWER(a.actionName) = LOWER(:actionName))
              AND (:tableName IS NULL OR LOWER(a.tableName) = LOWER(:tableName))
              AND (:userId IS NULL OR a.userId = :userId)
            """)
    Page<AuditLog> searchAuditLogs(
            @Param("actionName") String actionName,
            @Param("tableName") String tableName,
            @Param("userId") Integer userId,
            Pageable pageable
    );
}
