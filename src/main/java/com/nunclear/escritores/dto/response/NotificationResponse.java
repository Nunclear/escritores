package com.nunclear.escritores.dto.response;

import com.nunclear.escritores.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Integer id,
        Integer recipientUserId,
        NotificationType type,
        Integer relatedUserId,
        Integer relatedStoryId,
        Integer relatedChapterId,
        Integer relatedCommentId,
        String content,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
