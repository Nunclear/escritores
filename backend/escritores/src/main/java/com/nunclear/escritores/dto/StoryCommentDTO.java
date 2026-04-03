package com.nunclear.escritores.dto;

import com.nunclear.escritores.entity.Visibility;
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
public class StoryCommentDTO {
    private Integer id;
    private Integer storyId;
    private Integer chapterId;
    private Integer authorUserId;
    private String authorUserName;
    private Integer parentCommentId;
    private String content;
    private Visibility visibility;
    private LocalDateTime editedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
