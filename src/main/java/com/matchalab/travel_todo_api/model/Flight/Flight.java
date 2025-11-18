package com.matchalab.travel_todo_api.model.Flight;

import java.util.UUID;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
public class Flight {

    @Id
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    String flightNumber;

    // Airport departureAirport;

    // Airport arrivalAirport;

    String departureDateTimeIsoString;

    public Flight(String flightNumber, String departureDateTimeIsoString) {
        this.flightNumber = flightNumber;
        this.departureDateTimeIsoString = departureDateTimeIsoString;
    }
}
