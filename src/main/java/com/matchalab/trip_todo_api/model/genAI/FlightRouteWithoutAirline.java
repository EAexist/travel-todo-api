package com.matchalab.trip_todo_api.model.genAI;

import lombok.Builder;

@Builder
public record FlightRouteWithoutAirline(
        String departureAirportIATACode,
        String arrivalAirportIATACode) {
}