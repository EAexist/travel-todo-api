package com.matchalab.trip_todo_api.model.DTO;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.Builder;

/* https://github.com/projectlombok/lombok/issues/3883 */
@Builder
public record TripDTO(
        @Nullable UUID id,
        Boolean isInitialized,
        @Nullable String title,
        @Nullable String startDateIsoString,
        @Nullable String endDateIsoString,
        @Nullable List<DestinationDTO> destination,
        @Nullable List<TodoDTO> todolist) {

    public TripDTO(UUID id,
            Boolean isInitialized,
            String title,
            String startDateIsoString,
            String endDateIsoString,
            List<DestinationDTO> destination,
            List<TodoDTO> todolist) {
        this.id = id;
        this.isInitialized = isInitialized;
        this.title = title;
        this.startDateIsoString = startDateIsoString;
        this.endDateIsoString = endDateIsoString;
        this.destination = destination;
        this.todolist = todolist;
    }
}
