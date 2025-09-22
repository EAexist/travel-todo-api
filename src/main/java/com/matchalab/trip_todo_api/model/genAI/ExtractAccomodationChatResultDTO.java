package com.matchalab.trip_todo_api.model.genAI;

import com.matchalab.trip_todo_api.enums.AccomodationType;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ExtractAccomodationChatResultDTO(
        @Nullable String reservationDetailHrefLink,
        @Nullable String reservationNumberOrCode,
        String accomodationTitle,
        @Nullable String roomTitle,
        @Nullable int numberOfGuest,
        @Nullable String clientName,
        @Nullable String checkinDateISOString,
        @Nullable String checkoutDateISOString,
        @Nullable String checkinAvailableSinceThisTimeISOString,
        @Nullable String checkinAvailableUntilThisTimeISOString,
        @Nullable String checkoutDeadlineTimeISOString,
        @Nullable String location,
        AccomodationType accomodationType) {
}
