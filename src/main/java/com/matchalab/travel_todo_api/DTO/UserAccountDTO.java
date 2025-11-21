package com.matchalab.travel_todo_api.DTO;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Singular;

@Builder
public record UserAccountDTO(UUID id, @Nullable String nickname,
        @Singular("tripSummary") List<TripSummaryDTO> tripSummary, UUID activeTripId) {
}