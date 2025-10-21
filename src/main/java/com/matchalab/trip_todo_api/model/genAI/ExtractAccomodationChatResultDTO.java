package com.matchalab.trip_todo_api.model.genAI;

import com.matchalab.trip_todo_api.enums.AccomodationCategory;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ExtractAccomodationChatResultDTO(
        @Nullable String reservationDetailHrefLink,
        @Nullable String reservationNumberOrCode,
        String accomodationTitle,
        @Nullable String roomTitle,
        @Nullable int numberOfClient,
        @Nullable String clientName,
        @Nullable String checkinDateIsoString,
        @Nullable String checkoutDateIsoString,
        @Nullable String checkinAvailableSinceThisTimeIsoString,
        @Nullable String checkinAvailableUntilThisTimeIsoString,
        @Nullable String checkoutDeadlineTimeIsoString,
        @Nullable String location,
        AccomodationCategory accomodationCategory) {
}
