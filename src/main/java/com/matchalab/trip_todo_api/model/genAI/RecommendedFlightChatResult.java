package com.matchalab.trip_todo_api.model.genAI;

import java.util.List;

import lombok.Builder;

@Builder
public record RecommendedFlightChatResult(
        List<Flight> recommendedOutboundFlight,
        List<Flight> recommendedReturnFlight) {
}