package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reading_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "story_id"})
})
@Getter
@Setter
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "story_id", nullable = false)
    private Integer storyId;

    @Column(name = "last_chapter_id")
    private Integer lastChapterId;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "percentage_read", nullable = false)
    private Integer percentageRead = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.lastReadAt == null) {
            this.lastReadAt = now;
        }
        if (this.percentageRead == null) {
            this.percentageRead = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
