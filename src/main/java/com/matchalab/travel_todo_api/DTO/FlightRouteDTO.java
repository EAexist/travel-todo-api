package com.matchalab.travel_todo_api.DTO;

import com.matchalab.travel_todo_api.model.Flight.Airline;
import java.util.List;
import java.util.UUID;

public record FlightRouteDTO(
    UUID id, AirportDTO departure, AirportDTO arrival, List<Airline> airlines) {}
