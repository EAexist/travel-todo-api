package com.matchalab.trip_todo_api.model.genAI;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
public record Flight(
        String departureAirportIataCode,
        String arrivalAirportIataCode,
        String airlineCompanyIataCode) {
}