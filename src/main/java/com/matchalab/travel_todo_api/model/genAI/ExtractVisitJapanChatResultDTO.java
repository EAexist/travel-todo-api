package com.matchalab.travel_todo_api.model.genAI;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ExtractVisitJapanChatResultDTO(
        @Nullable String reservationDetailHrefLink,
        @Nullable String reservationNumberOrCode,
        @Nullable String reservationDateTimeIsoString) {
}