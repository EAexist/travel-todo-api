package com.matchalab.trip_todo_api.model.DTO;

import java.util.List;

import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Flight.Flight;
import com.matchalab.trip_todo_api.model.Flight.FlightTicket;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record ReservationImageAnalysisResult(
        @Nullable List<Accomodation> accomodation,
        @Nullable List<Flight> flight,
        @Nullable List<FlightTicket> flightTicket) {

    // public ReservationImageAnalysisResult(AccomodationDTO[] accomodationDTO) {
    // this(accomodationDTO, null, null);
    // }

    // public ReservationImageAnalysisResult(Flight[] flight) {
    // this(null, flight, null);
    // }

    // public ReservationImageAnalysisResult(FlightTicket[] flightTicket) {
    // this(null, null, flightTicket);
    // }
}