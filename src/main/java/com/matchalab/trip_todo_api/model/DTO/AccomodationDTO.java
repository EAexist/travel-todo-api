package com.matchalab.trip_todo_api.model.DTO;

import java.util.Map;
import java.util.UUID;

import com.matchalab.trip_todo_api.enums.AccomodationCategory;

import lombok.Builder;

@Builder
public record AccomodationDTO(
        UUID id,
        String title,
        String roomTitle,
        int numberOfClient,
        String clientName,
        String checkinDateIsoString,
        String checkoutDateIsoString,
        String checkinStartTimeIsoString,
        String checkinEndTimeIsoString,
        String checkoutTimeIsoString,
        String region,
        AccomodationCategory category,
        Map<String, String> links) {
}
