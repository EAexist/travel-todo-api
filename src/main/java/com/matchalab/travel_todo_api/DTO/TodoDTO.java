package com.matchalab.travel_todo_api.DTO;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TodoDTO(
    UUID id,
    int orderKey,
    String note,
    @Nullable String completeDateIsoString,
    @Nullable List<FlightRouteDTO> flightRoutes,
    TodoContentDTO content) {}
