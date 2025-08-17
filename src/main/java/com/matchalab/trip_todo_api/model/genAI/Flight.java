package com.matchalab.trip_todo_api.model.genAI;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
public record Flight(
        String departureAirportIATACode,
        String arrivalAirportIATACode,
        String airlineCompanyIATACode) {
}