package com.matchalab.trip_todo_api.DTO;

import java.util.UUID;

import org.openapitools.jackson.nullable.JsonNullable;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record TodoPatchDTO(
        UUID id,
        int orderKey,
        String note,
        @Nullable JsonNullable<String> completeDateIsoString,
        TodoContentDTO content) {
}
