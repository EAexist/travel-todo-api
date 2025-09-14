package com.matchalab.trip_todo_api.model.DTO;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.Builder;

/* https://github.com/projectlombok/lombok/issues/3883 */
@Builder
public record TripDTO(
        @Nullable String id,
        boolean isInitialized,
        @Nullable String title,
        @Nullable String startDateISOString,
        @Nullable String endDateISOString,
        @Nullable List<DestinationDTO> destination,
        @Nullable List<TodoDTO> todolist,
        @Nullable List<AccomodationDTO> accomodation) {

    public TripDTO(String id,
            boolean isInitialized,
            String title,
            String startDateISOString,
            String endDateISOString,
            List<DestinationDTO> destination,
            List<TodoDTO> todolist,
            List<AccomodationDTO> accomodation) {
        this.id = id;
        this.isInitialized = isInitialized;
        this.title = title;
        this.startDateISOString = startDateISOString;
        this.endDateISOString = endDateISOString;
        this.destination = destination;
        this.todolist = todolist;
        this.accomodation = accomodation;
    }
}
