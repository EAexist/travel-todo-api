package com.matchalab.trip_todo_api.model.Flight;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Builder(builderMethodName = "todoContentBuilder")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String flightNumber;

    Airport departureAirport;

    Airport arrivalAirport;

    String departureDateTimeISOString;
}
