package com.matchalab.trip_todo_api.model.genAI;

public record AnalyzeFlightBookingReservationDTO(
        String reservationDetailLink,
        String flightNumber,
        String departureAirportIATACode,
        String arrivalAirportIATACode,
        int numberOfPassenger,
        String[] passengerNames,
        String departureDateTimeISOString) {
}
