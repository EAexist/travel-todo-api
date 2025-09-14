package com.matchalab.trip_todo_api.model.DTO;

import lombok.Builder;

@Builder
public record TodoDTO(
        String id,
        int orderKey,
        String note,
        String completeDateISOString,
        TodoContentDTO content) {
}
