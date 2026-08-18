package com.matchalab.travel_todo_api.service.ChatModelService;

import com.matchalab.travel_todo_api.exception.AiQuotaExceededException;
import com.matchalab.travel_todo_api.exception.AiServiceUnavailableException;
import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import com.matchalab.travel_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.travel_todo_api.model.genAI.RecommendedFlightChatResult;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile({"ai"})
public class GeminiChatModelService implements ChatModelService {

  private final ChatModel chatModel;

  @Override
  public ExtractReservationChatResultDTO extractReservationFromText(String confirmationText) {

    String instructionUserMessage =
        "텍스트에서 예약 내역에 관한 내용과 링크를 포함한 부분들을 수정없이 추출하고 합쳐. 그리고 모든 예약 내역을 추출해.";
    return (callWithBeanOutput(
        generateTextAnalysisUserMessage(instructionUserMessage, confirmationText),
        new BeanOutputConverter<>(ExtractReservationChatResultDTO.class)));
  }

  @Override
  public ExtractReservationChatResultDTO classifyAccomodationCategory(String confirmationText) {

    String instructionUserMessage =
        "텍스트에서 예약 내역에 관한 내용과 링크를 포함한 부분들을 수정없이 추출하고 합쳐. 그리고 모든 예약 내역을 추출해.";
    return (callWithBeanOutput(
        generateTextAnalysisUserMessage(instructionUserMessage, confirmationText),
        new BeanOutputConverter<>(ExtractReservationChatResultDTO.class)));
  }

  @Override
  public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle) {

    String departureTitle = "한국";
    String language = "Korean";
    String template =
        "{departureTitle}에서 {destinationTitle}로 여행할 때 이용할 수 있는 모든 outbound/return 직항 항공 노선을 한국에서 많이 이용하는 순서대로 나열해.";

    Prompt prompt =
        new PromptTemplate(template)
            .create(
                Map.of(
                    "departureTitle",
                    departureTitle,
                    "destinationTitle",
                    destinationTitle,
                    "language",
                    language));

    RecommendedFlightChatResult recommendedFlight =
        callWithBeanOutput(
            prompt.getUserMessage().getText(),
            new BeanOutputConverter<>(RecommendedFlightChatResult.class));

    return recommendedFlight;
  }

  public List<String> getRecommendedAirline(FlightRoute flightRoute) {

    String language = "Korean";
    String template =
        "출발:{departureAirportIATA},도착:{destinationAirportIATA} 에 해당하는 모든 항공 노선 목록에 대해 각 노선을 운영하는 항공사의 Offical ICAO Code를 최대한 많이 알려줘. 노선의 한국인 이용객이 많은 순서대로 나열해.";

    Prompt prompt =
        new PromptTemplate(template)
            .create(
                Map.of(
                    "departureAirportIATA",
                    flightRoute.getDeparture().getIataCode(),
                    "destinationAirportIATA",
                    flightRoute.getArrival().getIataCode(),
                    "language",
                    language));

    List<String> recommendedAirlines =
        callWithBeanOutput(
            prompt.getUserMessage().getText(),
            new BeanOutputConverter<>(new ParameterizedTypeReference<List<String>>() {}));

    return recommendedAirlines;
  }

  private <T> T callWithBeanOutput(String message, BeanOutputConverter<T> outputConverter) {

    try {
      Prompt prompt =
          PromptTemplate.builder()
              .template(String.format("%s\n%s", "{format}", message))
              .variables(Map.of("format", outputConverter.getFormat()))
              .build()
              .create();

      String text = chatModel.call(prompt).getResult().getOutput().getText();
      return outputConverter.convert(text);

    } catch (NonTransientAiException e) {
      throw new AiQuotaExceededException();
    } catch (Exception e) {
      throw new AiServiceUnavailableException();
    }
  }

  private String generateTextAnalysisUserMessage(
      String instructionUserMessage, String confirmationText) {

    return String.format("%s\n%s", instructionUserMessage, confirmationText);
  }
}
