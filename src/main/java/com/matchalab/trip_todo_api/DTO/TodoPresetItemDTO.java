package com.matchalab.trip_todo_api.DTO;

public record TodoPresetItemDTO(
        Boolean isFlaggedToAdd,
        TodoContentDTO content) {
}