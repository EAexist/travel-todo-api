package com.matchalab.trip_todo_api.model.DTO;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UserAccountDTO(
        Long id,
        @Nullable String nickname,
        List<TripSummaryDTO> tripSummary) {
}