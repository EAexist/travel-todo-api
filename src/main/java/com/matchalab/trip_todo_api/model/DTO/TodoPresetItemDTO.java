package com.matchalab.trip_todo_api.model.DTO;

public record TodoPresetItemDTO(
        Boolean isFlaggedToAdd,
        TodoContentDTO content) {
}