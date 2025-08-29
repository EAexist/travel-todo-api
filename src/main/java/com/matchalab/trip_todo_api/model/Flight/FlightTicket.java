package com.matchalab.trip_todo_api.model.Flight;

import lombok.Builder;

@Builder
public record FlightTicket(
        Long id,
        String flightNumber,
        String departure,
        String arrival,
        String passengerName,
        String departureDateTimeISOString,
        String arrivalDateTimeISOString,
        String qrCodeFilePath) {
}
