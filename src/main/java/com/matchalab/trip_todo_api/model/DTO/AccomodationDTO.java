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
        String checkinDateISOString,
        String checkoutDateISOString,
        String checkinStartTimeISOString,
        String checkinEndTimeISOString,
        String checkoutTimeISOString,
        String region,
        String type,
        Map<String, String> links) {
}
