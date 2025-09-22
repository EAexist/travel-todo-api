package com.matchalab.trip_todo_api.model.Flight;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String flightNumber;

    // Airport departureAirport;

    // Airport arrivalAirport;

    String departureDateTimeISOString;
}
