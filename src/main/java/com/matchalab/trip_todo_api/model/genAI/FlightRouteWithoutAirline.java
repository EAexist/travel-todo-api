package com.matchalab.trip_todo_api.model.genAI;

import lombok.Builder;
import lombok.Getter;

@Builder
public record FlightRouteWithoutAirline(
        String departureAirportIATACode,
        String arrivalAirportIATACode) {
}