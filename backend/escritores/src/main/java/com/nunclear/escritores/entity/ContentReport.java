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
@Table(name = "content_report", indexes = {
        @Index(columnList = "reporter_user_id"),
        @Index(columnList = "target_user_id"),
        @Index(columnList = "story_id"),
        @Index(columnList = "chapter_id"),
        @Index(columnList = "comment_id"),
        @Index(columnList = "reviewed_by_user_id"),
        @Index(columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private AppUser reporterUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private AppUser targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private StoryComment comment;

    @NotBlank(message = "Reason is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reasonText;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_mod_id")
    private AppUser resolvedByMod;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
