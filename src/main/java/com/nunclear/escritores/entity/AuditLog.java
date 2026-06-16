package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_name", nullable = false, length = 30)
    private String actionName;

    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "entity_name", nullable = false, length = 150)
    private String entityName;

    @Column(name = "record_id", length = 80)
    private String recordId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", length = 150)
    private String username;

    @Column(name = "request_method", length = 20)
    private String requestMethod;

    @Column(name = "request_path", length = 500)
    private String requestPath;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
