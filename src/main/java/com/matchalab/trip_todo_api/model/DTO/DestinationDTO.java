package com.matchalab.trip_todo_api.model.DTO;

import lombok.Builder;

@Builder
public record DestinationDTO(
        String id,
        String title,
        String iso2DigitNationCode,
        String region,
        String description) {
}
