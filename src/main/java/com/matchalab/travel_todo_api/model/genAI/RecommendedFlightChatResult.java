package com.matchalab.travel_todo_api.model.genAI;

import java.util.List;

import lombok.Builder;

@Builder
public record RecommendedFlightChatResult(
        List<FlightRouteWithoutAirline> recommendedOutboundFlight,
        List<FlightRouteWithoutAirline> recommendedReturnFlight) {
}