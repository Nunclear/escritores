package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_change_history")
@SQLDelete(sql = "UPDATE user_change_history SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
public class UserChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "changed_field", nullable = false, length = 100)
    private String changedField;

    @Column(name = "old_value", length = 255)
    private String oldValue;

    @Column(name = "new_value", length = 255)
    private String newValue;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by_user_id", nullable = false)
    private Integer changedByUserId;


    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.changedAt == null) {
            this.changedAt = LocalDateTime.now();
        }
    }
}