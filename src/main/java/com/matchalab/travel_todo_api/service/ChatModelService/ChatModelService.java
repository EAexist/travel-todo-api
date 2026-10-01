package com.matchalab.travel_todo_api.service.ChatModelService;

import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import com.matchalab.travel_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.travel_todo_api.model.genAI.RecommendedFlightChatResult;
import java.util.List;

public interface ChatModelService {

  public ExtractReservationChatResultDTO extractReservationFromText(String confirmationText);

  public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle);

  public ExtractReservationChatResultDTO classifyAccomodationCategory(String confirmationText);

  public List<String> getRecommendedAirline(FlightRoute flightRoute);
}
