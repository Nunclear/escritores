package com.nunclear.escritores.dto.response;

import java.time.LocalDateTime;

public record GenreResponse(
        Integer id,
        String name,
        String slug,
        String description,
        String iconUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
