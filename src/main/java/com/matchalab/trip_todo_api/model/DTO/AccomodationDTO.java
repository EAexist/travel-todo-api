package com.matchalab.trip_todo_api.model.DTO;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;

@Builder
public record AccomodationDTO(
        UUID id,
        String title,
        String roomTitle,
        int numberOfGuest,
        String clientName,
        String checkinDateIsoString,
        String checkoutDateIsoString,
        String checkinStartTimeIsoString,
        String checkinEndTimeIsoString,
        String checkoutTimeIsoString,
        String region,
        String type,
        Map<String, String> links) {
}
