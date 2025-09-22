package com.matchalab.trip_todo_api.model.genAI;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ExtractGeneralReservationChatResultDTO(
        @Nullable String reservationDetailHrefLink,
        @Nullable String reservationNumberOrCode,
        String title,
        String note,
        @Nullable int numberOfClient,
        @Nullable String clientName,
        @Nullable String reservationDateTimeISOString) {
}