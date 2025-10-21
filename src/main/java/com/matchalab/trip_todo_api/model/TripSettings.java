package com.matchalab.trip_todo_api.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    private Boolean isTripMode = false;

    @Builder.Default
    private Boolean doSortReservationsByCategory = false;

    @Builder.Default
    private Boolean doHideCompletedTodo = true;

    @Builder.Default
    private Boolean doHideCompletedReservation = true;
}
