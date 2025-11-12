package com.matchalab.travel_todo_api.DTO;

import java.util.List;

import com.matchalab.travel_todo_api.model.Accomodation;
import com.matchalab.travel_todo_api.model.Flight.Flight;
import com.matchalab.travel_todo_api.model.Reservation.FlightTicket;

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