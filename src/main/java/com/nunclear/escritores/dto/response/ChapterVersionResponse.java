package com.nunclear.escritores.dto.response;

import java.time.LocalDateTime;

public record ChapterVersionResponse(
        Integer id,
        Integer chapterId,
        Integer versionNumber,
        String title,
        String subtitle,
        String content,
        LocalDateTime createdAt
) {
}
