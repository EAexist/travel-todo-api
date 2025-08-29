package com.matchalab.trip_todo_api.model.DTO;

import lombok.Builder;

@Builder
public record DestinationDTO(
        Long id,
        String title,
        String countryISO,
        String region,
        String description) {
}
