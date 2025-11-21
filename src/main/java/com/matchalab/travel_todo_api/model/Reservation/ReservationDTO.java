package com.matchalab.travel_todo_api.model.Reservation;

import java.util.UUID;

import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.model.Accomodation;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private UUID id;

    ReservationCategory category;
    Boolean isCompleted;

    @Nullable
    String primaryHrefLink;

    @Nullable
    String code;

    String note;

    @Nullable
    VisitJapan visitJapan;

    @Nullable
    Accomodation accomodation;

    @Nullable
    FlightBooking flightBooking;

    @Nullable
    FlightTicket flightTicket;

    @Nullable
    GeneralReservation generalReservation;
}