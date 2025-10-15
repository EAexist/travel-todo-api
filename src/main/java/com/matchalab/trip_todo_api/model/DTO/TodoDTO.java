package com.matchalab.trip_todo_api.model.DTO;

import java.util.UUID;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record TodoDTO(
        UUID id,
        int orderKey,
        String note,
        String completeDateIsoString,
        TodoContentDTO content) {
}
