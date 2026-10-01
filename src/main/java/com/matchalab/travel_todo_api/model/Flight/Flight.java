package com.matchalab.travel_todo_api.model.Flight;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
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

  String flightNumber;
  String departureDateTimeIsoString;

  // Airport departureAirport;

  // Airport arrivalAirport;
  @Id @NonNull @Builder.Default private UUID id = UUID.randomUUID();

  public Flight(String flightNumber, String departureDateTimeIsoString) {
    this.id = UUID.randomUUID();
    this.flightNumber = flightNumber;
    this.departureDateTimeIsoString = departureDateTimeIsoString;
  }
}
