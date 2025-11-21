package com.matchalab.travel_todo_api.DTO;

import java.util.Map;
import java.util.UUID;

import com.matchalab.travel_todo_api.enums.AccomodationCategory;

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
