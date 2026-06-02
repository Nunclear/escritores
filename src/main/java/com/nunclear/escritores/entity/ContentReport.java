package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.nunclear.escritores.entity.Auditable;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_report")
@Getter
@Setter
public class ContentReport extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reporter_user_id", nullable = false)
    private Integer reporterUserId;

    @Column(name = "target_user_id")
    private Integer targetUserId;

    @Column(name = "story_id")
    private Integer storyId;

    @Column(name = "chapter_id")
    private Integer chapterId;

    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "reason_text", nullable = false, columnDefinition = "TEXT")
    private String reasonText;

    @Column(name = "status_name", nullable = false, length = 30)
    private String statusName = "pending";

    @Column(name = "reviewed_by_user_id")
    private Integer reviewedByUserId;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "resolution_text", columnDefinition = "TEXT")
    private String resolutionText;

    // createdAt and updatedAt are inherited from Auditable

    @PrePersist
    public void prePersist() {
        // Auditable.onCreate() will set createdAt and updatedAt
        if (this.statusName == null || this.statusName.isBlank()) {
            this.statusName = "pending";
        }
    }
}