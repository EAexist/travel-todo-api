package com.matchalab.trip_todo_api.DTO;

import java.util.List;
import java.util.UUID;

import com.amadeus.Airline;

public record FlightRouteDTO(
        UUID id,
        AirportDTO departure,
        AirportDTO arrival,
        List<Airline> airlines) {
}
