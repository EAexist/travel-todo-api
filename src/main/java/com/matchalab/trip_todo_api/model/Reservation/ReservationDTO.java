package com.matchalab.trip_todo_api.model.Reservation;

import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.Accomodation;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationDTO {

    private String id;

    ReservationCategory category;
    // String rawText;

    @Nullable
    String code;

    String note;

    @Nullable
    String primaryHrefLink;

    @Nullable
    String serverFileUri;

    @Nullable
    String localAppStorageFileUri;

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