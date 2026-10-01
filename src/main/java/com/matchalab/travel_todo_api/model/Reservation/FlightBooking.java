package com.matchalab.travel_todo_api.model.Reservation;

import com.matchalab.travel_todo_api.model.Flight.Airport;
import lombok.Builder;

@Builder
public record FlightBooking(
    String flightNumber,
    String departureDateTimeIsoString,
    Airport departureAirport,
    Airport arrivalAirport,
    int numberOfPassenger,
    String passengerName)
    implements ReservationDetail {}
