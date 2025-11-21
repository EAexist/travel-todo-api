package com.matchalab.travel_todo_api.model.genAI;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ExtractFlightTicketChatResultDTO(
        @Nullable String reservationDetailHrefLink,
        @Nullable String reservationNumberOrCode,
        String flightNumber,
        @Nullable String departureAirportIataCode,
        @Nullable String arrivalAirportIataCode,
        @Nullable String passengerName,
        @Nullable String departureDateTimeIsoString) {
}
