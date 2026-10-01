package com.matchalab.travel_todo_api.model.Reservation;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record VisitJapan(@Nullable String dateTimeIsoString) implements ReservationDetail {}
