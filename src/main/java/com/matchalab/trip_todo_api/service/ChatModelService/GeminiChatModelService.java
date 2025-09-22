package com.matchalab.trip_todo_api.service.ChatModelService;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import com.google.cloud.vertexai.VertexAI;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile({ "prod" })
public class GeminiChatModelService implements ChatModelService {

    private final VertexAiGeminiChatModel chatModel = new VertexAiGeminiChatModel(new VertexAI(),
            VertexAiGeminiChatOptions.builder().model("gemini-2.0-flash-lite").build(),
            ToolCallingManager.builder().build(),
            new RetryTemplate(), null);

    // private final ChatModel chatModel;

    @Override
    public ExtractReservationChatResultDTO extractReservationFromText(String confirmationText) {

        String instructionUserMessage = "텍스트에서 예약 내역에 관한 내용과 링크를 포함한 부분들을 수정없이 추출하고 합쳐. 그리고 모든 예약 내역을 추출해.";
        return (callWithBeanOutput(
                generateTextAnalysisUserMessage(instructionUserMessage, confirmationText),
                new BeanOutputConverter<>(ExtractReservationChatResultDTO.class)));
    }

    @Override
    public ExtractReservationChatResultDTO classifyAccomodationType(String confirmationText) {

        String instructionUserMessage = "텍스트에서 예약 내역에 관한 내용과 링크를 포함한 부분들을 수정없이 추출하고 합쳐. 그리고 모든 예약 내역을 추출해.";
        return (callWithBeanOutput(
                generateTextAnalysisUserMessage(instructionUserMessage, confirmationText),
                new BeanOutputConverter<>(ExtractReservationChatResultDTO.class)));
    }

    @Override
    public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle) {

        String departureTitle = "한국";
        String language = "Korean";
        String template = "{departureTitle}에서 {destinationTitle}로 여행할 때 이용할 수 있는 모든 outbound/return 직항 항공 노선을 한국에서 많이 이용하는 순서대로 나열해.";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("departureTitle", departureTitle, "destinationTitle", destinationTitle, "language",
                        language));

        RecommendedFlightChatResult recommendedFlight = callWithBeanOutput(prompt.getUserMessage().getText(),
                new BeanOutputConverter<>(RecommendedFlightChatResult.class));

        return recommendedFlight;
    }

    public List<String> getRecommendedAirline(FlightRoute flightRoute) {

        String language = "Korean";
        String template = "출발:{departureAirportIATA},도착:{destinationAirportIATA} 에 해당하는 모든 항공 노선 목록에 대해 각 노선을 운영하는 항공사의 Offical IATA Code를 최대한 많이 알려줘. 노선의 한국인 이용객이 많은 순서대로 나열해.";

        Prompt prompt = new PromptTemplate(template)
                .create(Map.of("departureAirportIATA", flightRoute.getDeparture().getIATACode(),
                        "destinationAirportIATA", flightRoute.getArrival().getIATACode(), "language", language));

        List<String> recommendedAirlines = callWithBeanOutput(prompt.getUserMessage().getText(),
                new BeanOutputConverter<>(new ParameterizedTypeReference<List<String>>() {
                }));

        return recommendedAirlines;
    }

    private <T> T callWithBeanOutput(String message, BeanOutputConverter<T> outputConverter) {

        Prompt prompt = PromptTemplate.builder().template(String.format("%s\n%s", "{format}", message))
                .variables(Map.of("format", outputConverter.getFormat())).build().create();

        String text = chatModel.call(prompt).getResult().getOutput().getText();

        log.info("[callWithBeanOutput] text: " + text);

        return outputConverter.convert(text);
    }

    private String generateTextAnalysisUserMessage(String instructionUserMessage, String confirmationText) {

        return String.format("%s\n%s", instructionUserMessage, confirmationText);
    }

    /*
     * @depreacted Use Chatmodel instead.
     */
    // private final ChatClient chatClient;

    // public GeminiChatModelService(ChatClient chatClient) {
    // this.chatClient = chatClient;
    // }

    // public GeminiChatModelService(ChatClient.Builder chatClientBuilder) {
    // this.chatClient = chatClientBuilder.build();
    // }

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

    // public List<ExtractFlightBookingChatResultDTO>
    // extractFlightBookingFromText(String confirmationText) {

    // String instructionUserMessage = "텍스트에서 모든 항공권 예약 내역을 추출해.";
    // return (callWithBeanOutput(
    // generateTextAnalysisUserMessage(instructionUserMessage, confirmationText),
    // new
    // BeanOutputConverter<>(ExtractFlightBookingChatResultListDTO.class))).flightBooking();
    // }

    // public List<ExtractFlightTicketChatResultDTO>
    // extractFlightTicketReservationFromText(String confirmationText) {

    // String instructionUserMessage = "텍스트에서 모든 항공권을 추출해.";
    // return (callWithBeanOutput(
    // generateTextAnalysisUserMessage(instructionUserMessage, confirmationText),
    // new
    // BeanOutputConverter<>(ExtractFlightTicketChatResultListDTO.class))).flightTicket();
    // }

    // public List<ExtractAccomodationChatResultDTO>
    // extractAccomodationReservationFromText(String confirmationText) {

    // String instructionUserMessage = "텍스트에서 모든 숙박 예약 내역을 추출해.";
    // return (callWithBeanOutput(
    // generateTextAnalysisUserMessage(instructionUserMessage, confirmationText),
    // new
    // BeanOutputConverter<>(ExtractAccomodationChatResultListDTO.class))).accomodation();
    // }

    // public ReservationCategory classifyReservation(String confirmationText) {

    // String instructionUserMessage = String.format(
    // "Classify the following reservation:\n{confirmationText}\nProvide a single
    // word from this list: %s",
    // String.join(", ", Stream.of(ReservationCategoryChatResult.values())
    // .map(Enum::name)
    // .toArray(String[]::new)));

    // String classifiedCategoryString = chatModel.call(
    // generateTextAnalysisUserMessage(instructionUserMessage, confirmationText))
    // .trim();

    // log.info("[classifyReservation] classifiedCategoryString:" +
    // classifiedCategoryString);

    // ReservationCategoryChatResult classifiedCategory =
    // ReservationCategoryChatResult
    // .valueOf(classifiedCategoryString);

    // log.info("[classifyReservation] classifiedCategory:" +
    // classifiedCategory.name());

    // switch (classifiedCategory) {
    // case ReservationCategoryChatResult.NOT_RESERVATION:
    // throw new IllegalArgumentException("Not a Reservation:" +
    // classifiedCategory);
    // case ReservationCategoryChatResult.FLIGHT_RESERVATION:
    // return ReservationCategory.FLIGHT_BOOKING;
    // case ReservationCategoryChatResult.CONFIRMED_FLIGHT_TICKET:
    // return ReservationCategory.FLIGHT_TICKET;
    // case ReservationCategoryChatResult.ACCOMODATION:
    // return ReservationCategory.ACCOMODATION;
    // case ReservationCategoryChatResult.OTHER_RESERVATION:
    // return ReservationCategory.GENERAL;
    // default:
    // throw new IllegalArgumentException("Unknown category:" + classifiedCategory);
    // }
    // }

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

    // public <T> List<T> extractReservationfromText(String text, String
    // textContextTitle) {

    // String template = "다음 줄이 {textContextTitle}이라면 결과를 생성해. 찾을 수 없는 속성은 무시해.
    // {textContextTitle}이 아니면 무시해. {format}\nText: {reservationText}";

    // Prompt prompt = new PromptTemplate(template)
    // .create(Map.of("reservationText", text));

    // T reservationImageAnalysisResult = callWithBeanOutput(prompt);

    // return Arrays.asList(reservationImageAnalysisResult);
    // }
}
