package com.nunclear.escritores.entity;

import com.nunclear.escritores.enums.CompletionState;
import com.nunclear.escritores.enums.AgeRating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "story")
@Getter
@Setter
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "owner_user_id", nullable = false)
    private Integer ownerUserId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "slug_text", unique = true, length = 255)
    private String slugText;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "visibility_state", nullable = false, length = 30)
    private String visibilityState;

    @Column(name = "publication_state", nullable = false, length = 30)
    private String publicationState;

    @Column(name = "allow_feedback", nullable = false)
    private Boolean allowFeedback = true;

    @Column(name = "allow_scores", nullable = false)
    private Boolean allowScores = true;

    @Column(name = "started_on")
    private LocalDate startedOn;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "language", length = 10)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_state", length = 30)
    private CompletionState completionState;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_rating", length = 30)
    private AgeRating ageRating;

    @Column(name = "content_warnings", columnDefinition = "JSON")
    private String contentWarnings;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
