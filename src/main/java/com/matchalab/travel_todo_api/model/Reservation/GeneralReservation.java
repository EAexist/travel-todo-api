package com.matchalab.travel_todo_api.model.Reservation;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record GeneralReservation(
    String title,
    @Nullable int numberOfClient,
    @Nullable String clientName,
    @Nullable String dateTimeIsoString)
    implements ReservationDetail {}
