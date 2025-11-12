package com.matchalab.travel_todo_api.DTO;

public record TodoPresetItemDTO(
        Boolean isFlaggedToAdd,
        TodoContentDTO content) {
}