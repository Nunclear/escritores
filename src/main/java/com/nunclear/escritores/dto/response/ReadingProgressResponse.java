package com.nunclear.escritores.dto.response;

import java.time.LocalDateTime;

public record ReadingProgressResponse(
        Integer id,
        Integer userId,
        Integer storyId,
        Integer lastChapterId,
        LocalDateTime lastReadAt,
        Integer percentageRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
