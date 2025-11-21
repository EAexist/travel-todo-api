package com.matchalab.travel_todo_api.model.Reservation;

import java.util.UUID;

import org.openapitools.jackson.nullable.JsonNullable;

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
public class ReservationPatchDTO {

    private UUID id;

    ReservationCategory category;
    Boolean isCompleted;

    @Nullable
    String primaryHrefLink;

    @Nullable
    String code;

    String note;

    @Nullable
    JsonNullable<VisitJapan> visitJapan;

    @Nullable
    JsonNullable<Accomodation> accomodation;

    @Nullable
    JsonNullable<FlightBooking> flightBooking;

    @Nullable
    JsonNullable<FlightTicket> flightTicket;

    @Nullable
    JsonNullable<GeneralReservation> generalReservation;
}