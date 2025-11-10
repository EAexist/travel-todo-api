package com.matchalab.trip_todo_api.service.ChatModelService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.matchalab.trip_todo_api.enums.AccomodationCategory;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.ExtractAccomodationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightBookingChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractFlightTicketChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractGeneralReservationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.trip_todo_api.model.genAI.ExtractReservationChatResultDTO.ExtractReservationChatResultDTOBuilder;
import com.matchalab.trip_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;
import com.matchalab.trip_todo_api.service.HtmlParserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile({ "local", "local-init-data" })
public class MockChatModelService implements ChatModelService {

    @Autowired
    public HtmlParserService htmlParserService;

    @Override
    public ExtractReservationChatResultDTO extractReservationFromText(String confirmationText) {

        String parsedText = htmlParserService.extractTextAndLink(confirmationText);

        ExtractReservationChatResultDTOBuilder builder = ExtractReservationChatResultDTO.builder()
                .partOfTextAndLinksThatContainsReservationInformation(parsedText);

        log.info("[MockChatModelService] parsedText:" + parsedText);

        if (parsedText.contains("항공") && parsedText.contains("탑승권"))
            return builder
                    .flightTickets(List.of(
                            ExtractFlightTicketChatResultDTO.builder()
                                    .reservationDetailHrefLink(null)
                                    .reservationNumberOrCode("K9N96A")
                                    .flightNumber("ZE671")
                                    .departureAirportIataCode("ICN")
                                    .arrivalAirportIataCode("TKS")
                                    .departureDateTimeIsoString("2025-02-20")
                                    .passengerName("PYO HYEON")
                                    .build()))
                    .build();

        else if (parsedText.contains("항공") && parsedText.contains("예약"))
            return builder
                    .flightBookings(List.of(
                            ExtractFlightBookingChatResultDTO.builder()
                                    .reservationDetailHrefLink(
                                            "https://www.eastarjet.com/newstar/PGWRA00003?in_pnrNo=K9N96A")
                                    .reservationNumberOrCode("K9N96A")
                                    .flightNumber("ZE671")
                                    .departureAirportIataCode("ICN")
                                    .arrivalAirportIataCode("TKS")
                                    .numberOfPassenger(1)
                                    .departureDateTimeIsoString("2025-02-20")
                                    .passengerNames(new String[] { "PYO HYEON" })
                                    .build()))
                    .build();

        else if (parsedText.contains("숙소") && parsedText.contains("예약"))
            return builder
                    .accomodations(List.of(
                            ExtractAccomodationChatResultDTO.builder()
                                    .reservationDetailHrefLink(
                                            "https://agoda.onelink.me/1640755593?pid=redirect&c=CONFIRMATION_EMAIL_ONELINK&af_dp=agoda%3a%2f%2fmmb%2f%3fbookingToken%3dzPG19SiliyJJ1yAm5RPtQA%3d%3d&deep_link_value=agoda%3a%2f%2fmmb%2f%3fbookingToken%3dzPG19SiliyJJ1yAm5RPtQA%3d%3d&af_sub1=EXP-ID-AM-7093-B&af_sub3=bebf6434-a5d7-42ce-805f-3715787ae814&af_sub4=Hotel&af_force_deeplink=true&af_web_dp=https%3a%2f%2fwww.agoda.com%2faccount%2feditbooking.html%3fbookingId%3dzPG19SiliyJJ1yAm5RPtQA%3d%3d%26")
                                    .reservationNumberOrCode("1546592100")
                                    .accomodationTitle("HOSTEL PAQ tokushima")
                                    .roomTitle("도미토리 내 1인 예약 (혼성)")
                                    .numberOfClient(1)
                                    .clientName("PYO HYEON")
                                    .checkinDateIsoString("2025-02-24")
                                    .checkoutDateIsoString("2025-02-25")
                                    .checkinAvailableSinceThisTimeIsoString("15:00")
                                    .checkoutDeadlineTimeIsoString("10:00")
                                    .location("Tokushima city nakatouriimachi 2-5, 도쿠시마, 일본, 770-0844")
                                    .accomodationCategory(AccomodationCategory.DORMITORY)
                                    .build()))
                    .build();
        else
            return builder
                    .otherReservations(List.of(
                            ExtractGeneralReservationChatResultDTO.builder()
                                    .reservationDetailHrefLink(
                                            "https://www.etix.com/kketix/online/onlinereprint.jsp?userID=25504976&password=66755185")
                                    .reservationNumberOrCode("4428332754")
                                    .reservationTitle("Teshima Art Museum")
                                    .numberOfClient(1)
                                    .reservationDateTimeIsoString("2025-02-22")
                                    .build(),
                            ExtractGeneralReservationChatResultDTO.builder()
                                    .reservationDetailHrefLink(
                                            "https://www.etix.com/kketix/online/onlinereprint.jsp?userID=25504976&password=66755185")
                                    .reservationNumberOrCode("4428332898")
                                    .reservationTitle("Teshima Art Museum")
                                    .numberOfClient(1)
                                    .reservationDateTimeIsoString("2025-02-22")
                                    .build()))
                    .build();
    }

    @Override
    public RecommendedFlightChatResult getRecommendedFlight(String destinationTitle) {

        switch (destinationTitle) {
            case "교토":
            case "오사카":
                return RecommendedFlightChatResult.builder()
                        .recommendedOutboundFlight(List.of(FlightRouteWithoutAirline.builder()
                                .departureAirportIataCode("ICN").arrivalAirportIataCode("KIX").build()))
                        .recommendedReturnFlight(List.of(FlightRouteWithoutAirline.builder()
                                .departureAirportIataCode("KIX").arrivalAirportIataCode("ICN").build()))
                        .build();
            case "도쿄":
                return RecommendedFlightChatResult.builder()
                        .recommendedOutboundFlight(List.of(FlightRouteWithoutAirline.builder()
                                .departureAirportIataCode("ICN").arrivalAirportIataCode("NRT").build(),
                                FlightRouteWithoutAirline.builder()
                                        .departureAirportIataCode("ICN").arrivalAirportIataCode("HND").build()))
                        .recommendedReturnFlight(List.of(FlightRouteWithoutAirline.builder()
                                .departureAirportIataCode("NRT").arrivalAirportIataCode("ICN").build(),
                                FlightRouteWithoutAirline.builder()
                                        .departureAirportIataCode("HND").arrivalAirportIataCode("ICN").build()))
                        .build();
            case "제주":
                return RecommendedFlightChatResult.builder()
                        .recommendedOutboundFlight(List.of(FlightRouteWithoutAirline.builder()
                                .departureAirportIataCode("ICN").arrivalAirportIataCode("CJU").build(),
                                FlightRouteWithoutAirline.builder()
                                        .departureAirportIataCode("GMP").arrivalAirportIataCode("CJU").build()))
                        .recommendedReturnFlight(List.of(FlightRouteWithoutAirline.builder()
                                .departureAirportIataCode("CJU").arrivalAirportIataCode("ICN").build(),
                                FlightRouteWithoutAirline.builder()
                                        .departureAirportIataCode("CJU").arrivalAirportIataCode("GMP").build()))
                        .build();
            default:
                return RecommendedFlightChatResult.builder()
                        .build();
        }
    }

    @Override
    public ExtractReservationChatResultDTO classifyAccomodationCategory(String confirmationText) {

        return ExtractReservationChatResultDTO.builder().build();
    }

    @Override
    public List<String> getRecommendedAirline(FlightRoute flightRoute) {

        return List.of("KAL", "AAR", "JNA", "JJA", "TWB", "ABL", "ASV", "APJ", "JAL", "ANA");
    }
}
