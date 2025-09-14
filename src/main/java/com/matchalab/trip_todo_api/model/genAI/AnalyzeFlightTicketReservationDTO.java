package com.matchalab.trip_todo_api.model.genAI;

public record AnalyzeFlightTicketReservationDTO(
        String reservationDetailLink,
        String flightNumber,
        String departureAirportIATACode,
        String arrivalAirportIATACode,
        String passengerName,
        String departureDateTimeISOString) {
}
