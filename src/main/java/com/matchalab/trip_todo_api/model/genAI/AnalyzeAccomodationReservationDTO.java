package com.matchalab.trip_todo_api.model.genAI;

import com.matchalab.trip_todo_api.enums.AccomodationType;

public record AnalyzeAccomodationReservationDTO(
        String reservationDetailLink,
        String accomodationTitle,
        String roomTitle,
        int numberOfGuest,
        String clientName,
        String checkinDateISOString,
        String checkoutDateISOString,
        String checkinAvailableSinceThisTimeISOString,
        String checkinAvailableUntilThisTimeISOString,
        String checkoutDeadlineTimeISOString,
        String location,
        AccomodationType accomodationType) {
}
