package com.matchalab.travel_todo_api.model.genAI;

import lombok.Builder;

@Builder
public record FlightRouteWithoutAirline(
        String departureAirportIataCode,
        String arrivalAirportIataCode) {
}