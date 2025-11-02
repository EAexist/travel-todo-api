package com.matchalab.trip_todo_api.DTO;

import lombok.Builder;

@Builder
public record AirportDTO(
        String iataCode,
        String airportName,
        String cityName,
        String iso2DigitNationCode) {
}
