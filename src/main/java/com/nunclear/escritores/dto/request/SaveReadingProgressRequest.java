package com.nunclear.escritores.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record SaveReadingProgressRequest(
        Integer storyId,

        Integer lastChapterId,

        @Min(0)
        @Max(100)
        Integer percentageRead
) {
}
