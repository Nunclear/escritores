package com.nunclear.escritores.dto.request;

import jakarta.validation.constraints.NotNull;

public record RestoreChapterVersionRequest(
        @NotNull
        Integer versionNumber
) {
}
