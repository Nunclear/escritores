package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.nunclear.escritores.entity.Auditable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sanction")
@Getter
@Setter
public class UserSanction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "target_user_id", nullable = false)
    private Integer targetUserId;

    @Column(name = "applied_by_user_id", nullable = false)
    private Integer appliedByUserId;

    @Column(name = "sanction_kind", nullable = false, length = 30)
    private String sanctionKind;

    @Column(name = "reason_text", nullable = false, columnDefinition = "TEXT")
    private String reasonText;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // createdAt and updatedAt are inherited from Auditable

    @PrePersist
    public void prePersist() {
        // Auditable.onCreate() will set createdAt and updatedAt
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}