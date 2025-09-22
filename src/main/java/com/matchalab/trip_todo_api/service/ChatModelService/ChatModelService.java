package com.matchalab.trip_todo_api.service.ChatModelService;

import java.util.List;

import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;

public interface ChatModelService {

    public ExtractReservationChatResultDTO extractReservationFromText(String confirmationText);

    public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle);

    public ExtractReservationChatResultDTO classifyAccomodationType(String confirmationText);

    public List<String> getRecommendedAirline(FlightRoute flightRoute);
}
