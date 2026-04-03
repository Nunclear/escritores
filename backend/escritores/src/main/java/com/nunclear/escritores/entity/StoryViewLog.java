package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "story_view_log", indexes = {
    @Index(columnList = "story_id"),
    @Index(columnList = "chapter_id"),
    @Index(columnList = "user_id"),
    @Index(columnList = "viewed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryViewLog {
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
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(length = 255)
    private String visitorToken;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgentText;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        viewedAt = LocalDateTime.now();
    }
}
