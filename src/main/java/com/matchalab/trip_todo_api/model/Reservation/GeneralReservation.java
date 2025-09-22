package com.matchalab.trip_todo_api.model.Reservation;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GeneralReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
    @Nullable
    int numberOfClient;
    @Nullable
    String clientName;
    @Nullable
    String dateTimeISOString;
}
