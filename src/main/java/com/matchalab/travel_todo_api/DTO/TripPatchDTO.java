package com.matchalab.travel_todo_api.DTO;

import java.util.UUID;

import com.matchalab.travel_todo_api.model.TripSettings;

import jakarta.annotation.Nullable;
import lombok.Builder;

/* https://github.com/projectlombok/lombok/issues/3883 */
@Builder
public record TripPatchDTO(
        @Nullable UUID id,
        Boolean isInitialized,
        @Nullable String title,
        @Nullable String startDateIsoString,
        @Nullable String endDateIsoString,
        @Nullable TripSettings settings) {
}
