package com.matchalab.trip_todo_api.model.DTO;

import java.util.Map;

import lombok.Builder;

@Builder
public record AccomodationDTO(
        String id,
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
