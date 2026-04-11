package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "slug_text", length = 255)
    private String slugText;

    @Column(name = "visibility_state", nullable = false, length = 30)
    private String visibilityState;

    @Column(name = "publication_state", nullable = false, length = 30)
    private String publicationState;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}