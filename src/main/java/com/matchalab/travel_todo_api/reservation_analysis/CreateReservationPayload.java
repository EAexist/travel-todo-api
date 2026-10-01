package com.matchalab.travel_todo_api.reservation_analysis;

import com.matchalab.travel_todo_api.enums.ReservationCategory;
import lombok.Builder;

@Builder
public record CreateReservationPayload(ReservationCategory category, String confirmationText) {
}
