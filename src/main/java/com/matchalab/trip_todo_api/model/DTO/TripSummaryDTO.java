package com.matchalab.trip_todo_api.model.DTO;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.Builder;

/* https://github.com/projectlombok/lombok/issues/3883 */
@Builder
public record TripSummaryDTO(
        @Nullable UUID id,
        String createDateIsoString,
        boolean isInitialized,
        @Nullable String title,
        @Nullable String startDateIsoString,
        @Nullable String endDateIsoString,
        @Nullable List<String> destination) {

    public TripSummaryDTO(UUID id,
            String createDateIsoString,
            boolean isInitialized,
            String title,
            String startDateIsoString,
            String endDateIsoString,
            List<String> destination) {
        this.id = id;
        this.createDateIsoString = createDateIsoString;
        this.isInitialized = isInitialized;
        this.title = title;
        this.startDateIsoString = startDateIsoString;
        this.endDateIsoString = endDateIsoString;
        this.destination = destination;
    }
}
