package com.matchalab.trip_todo_api.model.genAI;

public record ExtractFlightBookingChatResultDTO(
        String reservationDetailLink,
        String flightNumber,
        String departureAirportIATACode,
        String arrivalAirportIATACode,
        int numberOfPassenger,
        String[] passengerNames,
        String departureDateTimeISOString) {
}
