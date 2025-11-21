package com.matchalab.travel_todo_api.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matchalab.travel_todo_api.model.TripSettings;
import com.matchalab.travel_todo_api.model.Reservation.ReservationDTO;

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
        @Nullable List<DestinationDTO> destinations,
        @Nullable List<TodoDTO> todolist,
        List<ReservationDTO> reservations,
        List<TodoContentDTO> stockTodoContents,
        @Nullable TripSettings settings) {

    public TripDTO(UUID id,
            Boolean isInitialized,
            String title,
            String startDateIsoString,
            String endDateIsoString,
            List<DestinationDTO> destinations,
            List<TodoDTO> todolist,
            List<ReservationDTO> reservations,
            List<TodoContentDTO> stockTodoContents, TripSettings settings) {
        this.id = id;
        this.isInitialized = isInitialized;
        this.title = title;
        this.startDateIsoString = startDateIsoString;
        this.endDateIsoString = endDateIsoString;
        this.destinations = destinations;
        this.todolist = todolist;
        this.reservations = reservations;
        this.stockTodoContents = stockTodoContents;
        this.settings = settings;
    }

    public static class TripDTOBuilder {
        private List<DestinationDTO> destinations = new ArrayList<DestinationDTO>();
        private List<TodoDTO> todolist = new ArrayList<TodoDTO>();
        private List<ReservationDTO> reservations = new ArrayList<ReservationDTO>();
    }
}
