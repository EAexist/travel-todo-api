package com.matchalab.trip_todo_api.model.Reservation;

import java.util.UUID;

import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class VisitJapan {

    @Id
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Nullable
    String dateTimeIsoString;
}