package com.nunclear.escritores.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignGenresToStoryRequest(
        @NotNull
        List<Integer> genreIds
) {
}
