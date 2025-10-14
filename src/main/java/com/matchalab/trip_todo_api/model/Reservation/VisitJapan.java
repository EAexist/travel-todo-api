package com.matchalab.trip_todo_api.model.Reservation;

import java.util.UUID;

import com.matchalab.trip_todo_api.generator.GeneratedOrCustomUUID;

import io.micrometer.common.lang.NonNull;
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
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Nullable
    String dateTimeIsoString;
}