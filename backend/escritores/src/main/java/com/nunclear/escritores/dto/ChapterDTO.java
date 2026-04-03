package com.nunclear.escritores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class ChapterDTO {
    private Integer id;
    private String title;
    private String subtitle;
    private String content;
    private LocalDate publishedOn;
    private Integer storyId;
    private Integer volumeId;
    private Integer positionIndex;
    private Integer readingMinutes;
    private Integer wordCount;
    private String publicationState;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;
}
