package com.matchalab.trip_todo_api.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
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
import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.DTO.ReservationImageAnalysisResult;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.ExtractAccomodationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightBookingChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightTicketChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenAIService {

    /*
     * @depreacted Use Chatclient instead.
     */
    // private final VertexAiGeminiChatModel geminiChatModel = new
    // VertexAiGeminiChatModel(new VertexAI(),
    // VertexAiGeminiChatOptions.builder().build(),
    // ToolCallingManager.builder().build(),
    // new RetryTemplate(), null);

    private final ChatClient chatClient;

    public GenAIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

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
    // extractInfofromReservationText(List<String> text) {

    // BeanOutputConverter<ReservationImageAnalysisResult> outputConverter = new
    // BeanOutputConverter<>(
    // new ParameterizedTypeReference<ReservationImageAnalysisResult>() {
    // });

    // String promptTemplate = "
    // 다음 줄의 예약 내역 텍스트로 ReservationImageAnalysisResult를 생성해.
    // ReservationImageAnalysisResult에 포함되는 새로운 예약 내역마다 다음 규칙에 따라 객체를 하나씩 생성해서 리스트
    // 속성에 추가해. {reservationTypeInstruction}
    // {format}\nText: {reservationText}
    // ";
    // String reservationTypeInstruction = "*예약 내역 종류 - 추가할 객체 이름"
    // + String.format("*%s - %s\n", "숙소 예약", "accomodationDTO")
    // + String.format("*%s - %s\n", "항공편 모바일 탑승권", "flightTicket")
    // + String.format("*%s - %s\n", "모바일 탑승권이 아닌 항공권 예약 내역", "flight");
    // Prompt prompt = new PromptTemplate(promptTemplate)
    // .create(Map.of("format", outputConverter.getFormat(),
    // "reservationTypeInstruction",
    // reservationTypeInstruction, "reservationText", text));

    // Generation resultgeneration = geminiChatModel.call(prompt).getResult();
    // ReservationImageAnalysisResult result = outputConverter
    // .convert(resultgeneration.getOutput().getText());

    // return result;
    // }

    /*
     * @depreacted Use callWithBeanOutput instead.
     */
    // private <T> T getConvertedChatModelResult(String text, String promptTemplate)
    // {

    // BeanOutputConverter<T> outputConverter = new BeanOutputConverter<>(
    // new ParameterizedTypeReference<T>() {
    // });

    // Prompt prompt = new PromptTemplate(promptTemplate)
    // .create(Map.of("format", outputConverter.getFormat(), "reservationText",
    // text));

    // Generation resultgeneration = geminiChatModel.call(prompt).getResult();
    // T result = outputConverter
    // .convert(resultgeneration.getOutput().getText());

    // return result;
    // }

    public <T> List<T> extractReservationfromText(List<String> text, String textContextTitle) {

        String template = "다음 줄이 {textContextTitle}이라면 결과를 생성해. 찾을 수 없는 속성은 무시해. {textContextTitle}이 아니면 무시해. {format}\nText: {reservationText}";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("reservationText", text));

        T reservationImageAnalysisResult = callWithBeanOutput(prompt);

        return Arrays.asList(reservationImageAnalysisResult);
    }

    public List<ExtractFlightBookingChatResultDTO> extractFlightBookingFromText(String textToAnalyze) {

        String promptTemplate = "항공권 예약 내역 텍스트에서 결과를 추출해.\n TextToAnalyze: {textToAnalyze}";
        return (callWithBeanOutput(generateTextAnalysisPrompt(textToAnalyze, promptTemplate)));
    }

    public List<ExtractFlightTicketChatResultDTO> extractFlightTicketReservationFromText(String textToAnalyze) {

        String promptTemplate = "항공권 텍스트에서 결과를 추출해.\n TextToAnalyze: {textToAnalyze}";
        return (callWithBeanOutput(generateTextAnalysisPrompt(textToAnalyze, promptTemplate)));
    }

    public List<ExtractAccomodationChatResultDTO> extractAccomodationReservationFromText(String textToAnalyze) {

        String promptTemplate = "숙박 예약 내역 텍스트에서 결과를 추출해.\n TextToAnalyze: {textToAnalyze}";
        return (callWithBeanOutput(generateTextAnalysisPrompt(textToAnalyze, promptTemplate)));
    }

    public ReservationCategory classifyReservation(String textToAnalyze) {

        String promptTemplate = "Classify the following reservation: \n TextToAnalyze: {textToAnalyze}";

        ReservationCategoryChatResultDTO result = callWithBeanOutput(
                generateTextAnalysisPrompt(textToAnalyze, promptTemplate));

        switch (result.category()) {
            case Not_Reservation:
                throw new IllegalArgumentException("Unknown status: " + result.category());
            case FlightReservation:
                return ReservationCategory.FlightBooking;
            case ConfirmedFlightTicket:
                return ReservationCategory.FlightTicket;
            case Accomodation:
                return ReservationCategory.Accomodation;
            case Other_Reservation:
                return ReservationCategory.General;
            default:
                throw new IllegalArgumentException("Unknown status: " + result.category());
        }
    }

    public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle) {

        String departureTitle = "한국";
        String language = "Korean";
        String template = "{departureTitle}에서 {destinationTitle}로 여행할 때 이용할 수 있는 모든 outbound/return 직항 항공 노선을 한국에서 많이 이용하는 순서대로 나열해.";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("departureTitle", departureTitle, "destinationTitle", destinationTitle, "language",
                        language));

        RecommendedFlightChatResult recommendedFlight = callWithBeanOutput(prompt);

        return recommendedFlight;
    }

    public List<String> getRecommendedAirline(FlightRoute flightRoute) {

        String language = "Korean";
        String template = "출발:{departureAirportIATA},도착:{destinationAirportIATA} 에 해당하는 모든 항공 노선 목록에 대해 각 노선을 운영하는 항공사의 Offical IATA Code를 최대한 많이 알려줘. 노선의 한국인 이용객이 많은 순서대로 나열해.";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("departureAirportIATA", flightRoute.getDeparture().getIATACode(),
                        "destinationAirportIATA", flightRoute.getArrival().getIATACode(), "language", language));

        List<String> recommendedAirlines = callWithBeanOutput(prompt);

        return recommendedAirlines;
    }

    private <T> T callWithBeanOutput(Prompt prompt) {

        BeanOutputConverter<T> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<T>() {
                });

        T result = chatClient.prompt(prompt).call().entity(new ParameterizedTypeReference<T>() {
        });

        return result;
    }

    private Prompt generateTextAnalysisPrompt(String textToAnalyze, String promptTemplate) {

        return new PromptTemplate(promptTemplate)
                .create(Map.of("textToAnalyze", textToAnalyze));
    }

    private enum ReservationCategoryChatResultEnum {
        Not_Reservation,
        FlightReservation,
        ConfirmedFlightTicket,
        Accomodation,
        Other_Reservation
    }

    private record ReservationCategoryChatResultDTO(
            ReservationCategoryChatResultEnum category) {
    }

    // public ReservationImageAnalysisResult
    // classifyAndExtractInfofromReservationText(List<String> text) {

    // BeanOutputConverter<ReservationCategory> outputConverter = new
    // BeanOutputConverter<>(
    // new ParameterizedTypeReference<ReservationCategory>() {
    // });

    // String classifyingPromptTemplate = "
    // 줄의 예약 내역 텍스트를 다음 타입 중 하나로 분류하고 해당하는 ReservationCategory값을 반환해.
    // {reservationTypeToExplanation}
    // {format}\nText: {reservationText}
    // ";
    // String reservationTypeToExplanation = String.format("{}\n",
    // ReservationCategory.Accomodation)
    // + String.format("{}: 항공편 모바일 탑승권\n", ReservationCategory.FlightTicket)
    // + String.format("{}: 모바일 탑승권이 아닌 항공권 예약 내역\n", ReservationCategory.Flight)
    // + String.format("{}: 이전 옵션 중 어느것도 해당하지 않음.\n", ReservationCategory.Invalid);
    // Prompt classifyingPrompt = new PromptTemplate(classifyingPromptTemplate)
    // .create(Map.of("format", classifyingOutputConverter.getFormat(),
    // "reservationTypeToExplanation",
    // reservationTypeToExplanation, "reservationText", text));

    // Generation classifyingResultgeneration =
    // geminiChatModel.call(classifyingPrompt).getResult();
    // ReservationCategory reservationType = classifyingOutputConverter
    // .convert(classifyingResultgeneration.getOutput().getText());

    // log.info(String.format("reservationType={}", reservationType));

    // BeanOutputConverter<ReservationImageAnalysisResult> outputConverter = new
    // BeanOutputConverter<>(
    // new ParameterizedTypeReference<ReservationImageAnalysisResult>() {
    // });
    // ReservationImageAnalysisResult result;

    // // switch (reservationType) {
    // // case ReservationCategory.Accomodation:
    // // // result =
    // extractStructuredReservationInfofromText<AccomodationDTO>(text)
    // // result = ReservationImageAnalysisResult.builder().accomodationDTO(
    // this.<AccomodationDTO>extractStructuredReservationInfofromText(text)).build();
    // // break;
    // // case ReservationCategory.FlightTicket:
    // // break;
    // // case ReservationCategory.Flight:
    // // break;
    // // case ReservationCategory.Invalid:
    // // break;
    // // default:
    // // break;
    // // }
    // }
}
