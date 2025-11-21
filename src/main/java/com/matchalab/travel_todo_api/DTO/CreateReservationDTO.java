package com.matchalab.travel_todo_api.DTO;

import com.matchalab.travel_todo_api.enums.ReservationCategory;

import lombok.Builder;

@Builder
public record CreateReservationDTO(
        ReservationCategory category,
        String confirmationText) {
}
