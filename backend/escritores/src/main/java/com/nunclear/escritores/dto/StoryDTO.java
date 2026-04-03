package com.nunclear.escritores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryDTO {
    private Integer id;
    private Integer ownerUserId;
    private String ownerUserName;
    private String title;
    private String slugText;
    private String description;
    private String coverImageUrl;
    private String visibilityState;
    private String publicationState;
    private Boolean allowFeedback;
    private Boolean allowScores;
    private LocalDate startedOn;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;
}
