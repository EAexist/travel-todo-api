package com.matchalab.trip_todo_api.model.DTO;

import lombok.Builder;

@Builder
public record CreateReservationDTO(
        String category,
        String confirmationText) {
}
