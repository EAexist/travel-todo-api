package com.matchalab.trip_todo_api.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import com.google.cloud.vertexai.VertexAI;
import com.matchalab.trip_todo_api.model.DTO.ReservationImageAnalysisResult;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenAIService {

    private final VertexAiGeminiChatModel geminiChatModel = new VertexAiGeminiChatModel(new VertexAI(),
            VertexAiGeminiChatOptions.builder().model("gemini-2.0-flash-lite").build(),
            ToolCallingManager.builder().build(),
            new RetryTemplate(), null);

    /**
     * This method downloads an image from a URL and sends its contents to the
     * Vision API for label
     * detection.
     *
     * @param imageUrl the URL of the image
     * @param map      the model map to use
     * @return a string with the list of labels and percentage of certainty
     * @throws com.google.cloud.spring.vision.CloudVisionException if the Vision
     *                                                             API
     *                                                             call produces an
     *                                                             error
     */

    // public ReservationImageAnalysisResult
    // extractReservationInfofromText(List<String> text) {

    // BeanOutputConverter<ReservationImageAnalysisResult> outputConverter = new
    // BeanOutputConverter<>(
    // new ParameterizedTypeReference<ReservationImageAnalysisResult>() {
    // });

    // String format = outputConverter.getFormat();
    // String template = """
    // Instruction: 다음 Text로 ReservationImageAnalysisResult를 생성해. 항공권 예약 내역이라면
    // Flight만 생성하고, 숙소 예약 내역이라면 AccomodationDTO만 생성해. 찾을 수 없는 속성은 무시해.
    // {format}\nText: {reservationText}
    // """;

    // Prompt prompt = new PromptTemplate(template)
    // .create(Map.of("format", format, "reservationText", text));

    // Generation generation = geminiChatModel.call(prompt).getResult();

    // ReservationImageAnalysisResult reservationImageAnalysisResult =
    // outputConverter
    // .convert(generation.getOutput().getText());

    // return reservationImageAnalysisResult;
    // }

    public ReservationImageAnalysisResult extractInfofromReservationText(List<String> text) {

        BeanOutputConverter<ReservationImageAnalysisResult> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<ReservationImageAnalysisResult>() {
                });

        String promptTemplate = """
                다음 줄의 예약 내역 텍스트로 ReservationImageAnalysisResult를 생성해. ReservationImageAnalysisResult에 포함되는 새로운 예약 내역마다 다음 규칙에 따라 객체를 하나씩 생성해서 리스트 속성에 추가해. {reservationTypeInstruction}
                {format}\nText: {reservationText}
                """;
        String reservationTypeInstruction = "*예약 내역 종류 - 추가할 객체 이름"
                + String.format("*%s - %s\n", "숙소 예약", "accomodationDTO")
                + String.format("*%s - %s\n", "항공편 모바일 탑승권", "flightTicket")
                + String.format("*%s - %s\n", "모바일 탑승권이 아닌 항공권 예약 내역", "flight");
        Prompt prompt = new PromptTemplate(promptTemplate)
                .create(Map.of("format", outputConverter.getFormat(), "reservationTypeInstruction",
                        reservationTypeInstruction, "reservationText", text));

        Generation resultgeneration = geminiChatModel.call(prompt).getResult();
        ReservationImageAnalysisResult result = outputConverter
                .convert(resultgeneration.getOutput().getText());

        return result;

        // switch (reservationType) {
        // case ReservationType.Accomodation:
        // // result = extractStructuredReservationInfofromText<AccomodationDTO>(text)
        // result = ReservationImageAnalysisResult.builder().accomodationDTO(
        // this.<AccomodationDTO>extractStructuredReservationInfofromText(text)).build();
        // break;
        // case ReservationType.FlightTicket:
        // break;
        // case ReservationType.Flight:
        // break;
        // case ReservationType.Invalid:
        // break;
        // default:
        // break;
        // }
    }

    public <T> List<T> extractReservationfromText(List<String> text, String textContextTitle) {

        BeanOutputConverter<T> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<T>() {
                });

        String format = outputConverter.getFormat();
        String template = """
                다음 줄이 {textContextTitle}이라면 결과를 생성해. 찾을 수 없는 속성은 무시해. {textContextTitle}이 아니면 무시해.
                        {format}\nText: {reservationText}
                        """;

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("format", format, "reservationText", text));

        Generation generation = geminiChatModel.call(prompt).getResult();

        T reservationImageAnalysisResult = outputConverter
                .convert(generation.getOutput().getText());

        return Arrays.asList(reservationImageAnalysisResult);
    }

    public List<FlightRoute> getFlightRoute(String destinationTitle) {

        BeanOutputConverter<List<FlightRoute>> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<FlightRoute>>() {
                });

        String format = outputConverter.getFormat();
        String departureTitle = "한국";
        String language = "Korean";
        String template = """
                {departureTitle}에서 {destinationTitle}로 여행할 때 직항 항공 노선을 이용할거야. 출발 공항과 도착 공항의 짝을 알려줘. 최대한 많이 알려줘. 한국인이 많이 이용하는 순서대로 나열해.
                {format} Provide Answer in {language}""";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("departureTitle", departureTitle, "destinationTitle", destinationTitle, "format",
                        format, "language", language));

        Generation generation = geminiChatModel.call(prompt).getResult();

        List<FlightRoute> recommendedFlight = outputConverter
                .convert(generation.getOutput().getText());

        return recommendedFlight;
    }

    public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle) {

        BeanOutputConverter<RecommendedFlightChatResult> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<RecommendedFlightChatResult>() {
                });

        String format = outputConverter.getFormat();
        String departureTitle = "한국";
        String language = "Korean";
        String template = """
                {departureTitle}에서 {destinationTitle}로 여행할 때 이용할 수 있는 모든 outbound/return 직항 항공 노선을 한국에서 많이 이용하는 순서대로 나열해.
                {format}""";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("departureTitle", departureTitle, "destinationTitle", destinationTitle, "format",
                        format, "language", language));

        Generation generation = geminiChatModel.call(prompt).getResult();

        RecommendedFlightChatResult recommendedFlight = outputConverter
                .convert(generation.getOutput().getText());

        return recommendedFlight;
    }

    public List<String> getRecommendedAirline(FlightRoute flightRoute) {

        BeanOutputConverter<List<String>> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<String>>() {
                });

        String format = outputConverter.getFormat();
        String language = "Korean";
        String template = """
                출발:{departureAirportIATA},도착:{destinationAirportIATA} 에 해당하는 모든 항공 노선 목록에 대해 각 노선을 운영하는 항공사의 Offical IATA Code를 최대한 많이 알려줘. 노선의 한국인 이용객이 많은 순서대로 나열해.
                {format}""";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("departureAirportIATA", flightRoute.getDeparture().getIATACode(),
                        "destinationAirportIATA", flightRoute.getArrival().getIATACode(), "format",
                        format, "language", language));

        Generation generation = geminiChatModel.call(prompt).getResult();

        List<String> recommendedAirlines = outputConverter
                .convert(generation.getOutput().getText());

        return recommendedAirlines;
    }

    // public ReservationImageAnalysisResult
    // classifyAndExtractInfofromReservationText(List<String> text) {

    // BeanOutputConverter<ReservationType> outputConverter = new
    // BeanOutputConverter<>(
    // new ParameterizedTypeReference<ReservationType>() {
    // });

    // String classifyingPromptTemplate = """
    // 줄의 예약 내역 텍스트를 다음 타입 중 하나로 분류하고 해당하는 ReservationType값을 반환해.
    // {reservationTypeToExplanation}
    // {format}\nText: {reservationText}
    // """;
    // String reservationTypeToExplanation = String.format("{}\n",
    // ReservationType.Accomodation)
    // + String.format("{}: 항공편 모바일 탑승권\n", ReservationType.FlightTicket)
    // + String.format("{}: 모바일 탑승권이 아닌 항공권 예약 내역\n", ReservationType.Flight)
    // + String.format("{}: 이전 옵션 중 어느것도 해당하지 않음.\n", ReservationType.Invalid);
    // Prompt classifyingPrompt = new PromptTemplate(classifyingPromptTemplate)
    // .create(Map.of("format", classifyingOutputConverter.getFormat(),
    // "reservationTypeToExplanation",
    // reservationTypeToExplanation, "reservationText", text));

    // Generation classifyingResultgeneration =
    // geminiChatModel.call(classifyingPrompt).getResult();
    // ReservationType reservationType = classifyingOutputConverter
    // .convert(classifyingResultgeneration.getOutput().getText());

    // log.info(String.format("reservationType={}", reservationType));

    // BeanOutputConverter<ReservationImageAnalysisResult> outputConverter = new
    // BeanOutputConverter<>(
    // new ParameterizedTypeReference<ReservationImageAnalysisResult>() {
    // });
    // ReservationImageAnalysisResult result;

    // // switch (reservationType) {
    // // case ReservationType.Accomodation:
    // // // result =
    // extractStructuredReservationInfofromText<AccomodationDTO>(text)
    // // result = ReservationImageAnalysisResult.builder().accomodationDTO(
    // this.<AccomodationDTO>extractStructuredReservationInfofromText(text)).build();
    // // break;
    // // case ReservationType.FlightTicket:
    // // break;
    // // case ReservationType.Flight:
    // // break;
    // // case ReservationType.Invalid:
    // // break;
    // // default:
    // // break;
    // // }
    // }
}
