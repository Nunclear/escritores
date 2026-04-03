package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sanction", indexes = {
    @Index(columnList = "user_id"),
    @Index(columnList = "applied_by_id"),
    @Index(columnList = "status"),
    @Index(columnList = "type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSanction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_by_id", nullable = false)
    private AppUser appliedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SanctionType type;

    @NotBlank(message = "Reason is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SanctionStatus status = SanctionStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "lifted_at")
    private LocalDateTime liftedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
