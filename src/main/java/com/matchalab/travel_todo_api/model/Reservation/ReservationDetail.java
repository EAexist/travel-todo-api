package com.matchalab.travel_todo_api.model.Reservation;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public sealed interface ReservationDetail
        permits VisitJapan,
        Accomodation,
        FlightBooking,
        FlightTicket,
        GeneralReservation {
}