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
@Table(name = "story", indexes = {
    @Index(columnList = "owner_user_id"),
    @Index(columnList = "visibility_state"),
    @Index(columnList = "publication_state")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private AppUser ownerUser;

    @NotBlank(message = "Title is required")
    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 255, unique = true)
    private String slugText;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String coverImageUrl;

    @Default
    @Column(length = 30, nullable = false)
    private String visibilityState = "public";

    @Default
    @Column(length = 30, nullable = false)
    private String publicationState = "draft";

    @Default
    @Column(nullable = false)
    private Boolean allowFeedback = true;

    @Default
    @Column(nullable = false)
    private Boolean allowScores = true;

    @Column(name = "started_on")
    private LocalDate startedOn;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

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
