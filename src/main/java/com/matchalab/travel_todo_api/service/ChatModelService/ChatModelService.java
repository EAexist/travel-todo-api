package com.matchalab.travel_todo_api.service.ChatModelService;

import java.util.List;

import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import com.matchalab.travel_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.travel_todo_api.model.genAI.RecommendedFlightChatResult;

public interface ChatModelService {

    public ExtractReservationChatResultDTO extractReservationFromText(String confirmationText);

    public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle);

    public ExtractReservationChatResultDTO classifyAccomodationCategory(String confirmationText);

    public List<String> getRecommendedAirline(FlightRoute flightRoute);
}
