package com.matchalab.trip_todo_api.model.DTO;

import com.matchalab.trip_todo_api.model.Todo.PresetTodoContent;

import lombok.Builder;

@Builder
public record PresetDTO(
        Boolean isFlaggedToAdd,
        PresetTodoContent todo) {
}

// @Builder
// record PresetTodoContentDTO(
// Long id,
// String category,
// String type,
// String title,
// Icon icon) {
// }
