package com.matchalab.trip_todo_api.model.DTO;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.Builder;

/* https://github.com/projectlombok/lombok/issues/3883 */
@Builder
public record TripSummaryDTO(
        @Nullable String id,
        boolean isInitialized,
        @Nullable String title,
        @Nullable String startDateISOString,
        @Nullable String endDateISOString,
        @Nullable List<String> destination) {

    public TripSummaryDTO(String id,
            boolean isInitialized,
            String title,
            String startDateISOString,
            String endDateISOString,
            List<String> destination) {
        this.id = id;
        this.isInitialized = isInitialized;
        this.title = title;
        this.startDateISOString = startDateISOString;
        this.endDateISOString = endDateISOString;
        this.destination = destination;
    }
}
