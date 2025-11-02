package com.matchalab.trip_todo_api.DTO;

import lombok.Builder;

@Builder
public record ResourceQuotaFetchDTO(
        int maxTrips,
        int maxTripDurationDays,
        int maxDestinations,
        int maxTodos,
        int maxReservations) {
}