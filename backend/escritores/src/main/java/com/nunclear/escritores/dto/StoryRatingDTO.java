package com.nunclear.escritores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryRatingDTO {
    private Integer id;
    private Integer storyId;
    private Integer authorUserId;
    private String authorUserName;
    private Integer scoreValue;
    private String reviewText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
