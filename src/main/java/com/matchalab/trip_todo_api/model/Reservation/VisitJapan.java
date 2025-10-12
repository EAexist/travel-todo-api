package com.matchalab.trip_todo_api.model.Reservation;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class VisitJapan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Nullable
    String dateTimeIsoString;
}