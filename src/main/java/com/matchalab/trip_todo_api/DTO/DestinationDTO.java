package com.matchalab.trip_todo_api.DTO;

import java.util.UUID;

import lombok.Builder;

@Builder
public record DestinationDTO(
        UUID id,
        String title,
        String iso2DigitNationCode,
        String region,
        String description) {
}
