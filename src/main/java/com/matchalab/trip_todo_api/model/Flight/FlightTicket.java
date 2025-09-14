package com.matchalab.trip_todo_api.model.Flight;

import jakarta.persistence.ManyToOne;
import lombok.Builder;

@Builder
public class FlightTicket extends Flight {

    @ManyToOne
    Airport departureAirport;

    @ManyToOne
    Airport arrivalAirport;

    String passengerName;
    String qrCodeFilePath;
}
