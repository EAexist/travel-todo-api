package com.matchalab.trip_todo_api.model.genAI;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ExtractFlightBookingChatResultDTO(
        @Nullable String reservationDetailHrefLink,
        @Nullable String reservationNumberOrCode,
        String flightNumber,
        @Nullable String departureAirportIATACode,
        @Nullable String arrivalAirportIATACode,
        @Nullable int numberOfPassenger,
        @Nullable String[] passengerNames,
        @Nullable String departureDateTimeISOString) {
}
