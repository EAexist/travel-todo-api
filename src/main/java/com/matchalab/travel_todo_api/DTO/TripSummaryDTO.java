package com.matchalab.travel_todo_api.DTO;

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
        boolean isSample,
        @Nullable String title,
        @Nullable String startDateIsoString,
        @Nullable String endDateIsoString,
        @Nullable List<String> destinationTitles) {

    public TripSummaryDTO(UUID id,
            String createDateIsoString,
            boolean isInitialized,
            boolean isSample,
            String title,
            String startDateIsoString,
            String endDateIsoString,
            List<String> destinationTitles) {
        this.id = id;
        this.createDateIsoString = createDateIsoString;
        this.isInitialized = isInitialized;
        this.isSample = isSample;
        this.title = title;
        this.startDateIsoString = startDateIsoString;
        this.endDateIsoString = endDateIsoString;
        this.destinationTitles = destinationTitles;
    }
}
