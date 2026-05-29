package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "story_genre", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"story_id", "genre_id"})
})
@Getter
@Setter
public class StoryGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "story_id", nullable = false)
    private Integer storyId;

    @Column(name = "genre_id", nullable = false)
    private Integer genreId;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @PrePersist
    public void prePersist() {
        if (this.addedAt == null) {
            this.addedAt = LocalDateTime.now();
        }
    }
}
