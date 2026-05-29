package com.nunclear.escritores.dto.response;

import java.time.LocalDateTime;

public record CommentLikeResponse(
        Integer id,
        Integer commentId,
        Integer userId,
        LocalDateTime createdAt
) {
}
