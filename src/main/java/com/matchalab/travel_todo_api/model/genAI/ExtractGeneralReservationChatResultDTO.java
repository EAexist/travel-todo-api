package com.matchalab.travel_todo_api.model.genAI;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record ExtractGeneralReservationChatResultDTO(
    @Nullable String reservationDetailHrefLink,
    @Nullable String reservationNumberOrCode,
    String reservationTitle,
    @Nullable int numberOfClient,
    @Nullable List<String> clientNames,
    @Nullable String reservationDateTimeIsoString) {}
