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
@Table(name = "story_comment", indexes = {
    @Index(columnList = "story_id"),
    @Index(columnList = "chapter_id"),
    @Index(columnList = "author_user_id"),
    @Index(columnList = "parent_comment_id"),
    @Index(columnList = "visibility")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private AppUser authorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private StoryComment parentComment;

    @NotBlank(message = "Content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Visibility visibility = Visibility.VISIBLE;

    @Column(name = "hide_reason")
    private String hideReason;

    @Column(name = "hidden_at")
    private LocalDateTime hiddenAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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
