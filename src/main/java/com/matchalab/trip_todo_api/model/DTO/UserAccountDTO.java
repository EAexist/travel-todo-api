package com.matchalab.trip_todo_api.model.DTO;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Singular;

@Builder
public record UserAccountDTO(String id, @Nullable String nickname,
        @Singular("tripSummary") List<TripSummaryDTO> tripSummary, String activeTripId) {
}