package com.matchalab.travel_todo_api.model.genAI;

import lombok.Builder;

@Builder
public record Flight(
        String departureAirportIataCode,
        String arrivalAirportIataCode,
        String airlineCompanyIataCode) {
}