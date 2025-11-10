package com.matchalab.trip_todo_api.DTO;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record TodoDTO(
        UUID id,
        int orderKey,
        String note,
        @Nullable String completeDateIsoString,
        @Nullable List<FlightRouteDTO> flightRoutes,
        TodoContentDTO content) {
}
