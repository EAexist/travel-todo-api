package com.matchalab.travel_todo_api.model.Reservation;

import com.matchalab.travel_todo_api.model.Flight.Airport;
import lombok.Builder;

@Builder
public record FlightTicket(
    String flightNumber,
    String departureDateTimeIsoString,
    Airport departureAirport,
    Airport arrivalAirport,
    String passengerName)
    implements ReservationDetail {}
