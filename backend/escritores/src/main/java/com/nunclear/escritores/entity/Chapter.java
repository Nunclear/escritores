package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapter", indexes = {
    @Index(columnList = "story_id"),
    @Index(columnList = "volume_id"),
    @Index(columnList = "publication_state")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title is required")
    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "published_on")
    private LocalDate publishedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volume_id")
    private Volume volume;

    @Column(name = "position_index")
    private Integer positionIndex;

    @Column(name = "reading_minutes")
    private Integer readingMinutes;

    @Column(name = "word_count")
    private Integer wordCount;

    @Default
    @Column(length = 30, nullable = false)
    private String publicationState = "draft";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

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
