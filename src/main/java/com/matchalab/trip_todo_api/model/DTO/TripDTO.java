package com.matchalab.trip_todo_api.model.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matchalab.trip_todo_api.model.Reservation.ReservationDTO;

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
        @Nullable List<TodoDTO> todolist, List<ReservationDTO> reservations) {

    public TripDTO(UUID id,
            Boolean isInitialized,
            String title,
            String startDateIsoString,
            String endDateIsoString,
            List<DestinationDTO> destination,
            List<TodoDTO> todolist,
            List<ReservationDTO> reservations) {
        this.id = id;
        this.isInitialized = isInitialized;
        this.title = title;
        this.startDateIsoString = startDateIsoString;
        this.endDateIsoString = endDateIsoString;
        this.destination = destination;
        this.todolist = todolist;
        this.reservations = reservations;
    }

    public static class TripDTOBuilder {
        private List<DestinationDTO> destination = new ArrayList<DestinationDTO>();
        private List<TodoDTO> todolist = new ArrayList<TodoDTO>();
        private List<ReservationDTO> reservations = new ArrayList<ReservationDTO>();
    }
}
