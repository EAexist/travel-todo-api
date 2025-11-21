package com.matchalab.travel_todo_api.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.matchalab.travel_todo_api.enums.AccomodationCategory;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.factory.AirportFactory;
import com.matchalab.travel_todo_api.model.Accomodation;
import com.matchalab.travel_todo_api.model.Reservation.FlightBooking;
import com.matchalab.travel_todo_api.model.Reservation.FlightTicket;
import com.matchalab.travel_todo_api.model.Reservation.GeneralReservation;
import com.matchalab.travel_todo_api.model.Reservation.Reservation;
import com.matchalab.travel_todo_api.model.genAI.ExtractFlightTicketChatResultDTO;
import com.matchalab.travel_todo_api.service.HtmlParserService;

import lombok.extern.slf4j.Slf4j;

@TestConfiguration
@Slf4j
public class MockReservationConfig {

    @Autowired
    public HtmlParserService htmlParserService;

    private String parseReservationText(String resourcePath) {

        ClassPathResource resource = new ClassPathResource(resourcePath);

        try {
            String confirmationText = StreamUtils.copyToString(resource.getInputStream(),
                    StandardCharsets.UTF_8);
            return htmlParserService.extractTextAndLink(confirmationText);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    ExtractFlightTicketChatResultDTO extractFlightTicketChatResultDTO_ZE671() {
        return ExtractFlightTicketChatResultDTO.builder()
                .reservationDetailHrefLink(null)
                .reservationNumberOrCode("K9N96A")
                .flightNumber("ZE671")
                .departureAirportIataCode("ICN")
                .arrivalAirportIataCode("TKS")
                .departureDateTimeIsoString("2025-02-20")
                .passengerName("PYO HYEON")
                .build();
    }

    @Bean
    Reservation reservation_flightTicket_Eastarjet_ZE671() {
        return Reservation.builder()
                .category(ReservationCategory.FLIGHT_TICKET)
                .rawText(parseReservationText("text/flightTicket/eastarjet/kakao_text_ko.txt"))
                .primaryHrefLink(null)
                .code("K9N96A")
                .flightTicket(
                        FlightTicket.builder()
                                .flightNumber("ZE671")
                                .departureDateTimeIsoString("2025-02-20")
                                .departureAirport(AirportFactory.createValidAirport("ICN"))
                                .arrivalAirport(AirportFactory.createValidAirport("TKS"))
                                .passengerName("PYO HYEON")
                                .build())
                .build();
    }

    private Reservation.ReservationBuilder reservation_flightBooking_Eastarjet_ZE671_builder = Reservation.builder()
            .category(ReservationCategory.FLIGHT_BOOKING)
            .primaryHrefLink("https://www.eastarjet.com/newstar/PGWRA00003?in_pnrNo=K9N96A")
            .code("K9N96A")
            .flightBooking(
                    FlightBooking.builder()
                            .flightNumber("ZE671")
                            .departureDateTimeIsoString("2025-02-20")
                            .departureAirport(AirportFactory.createValidAirport("ICN"))
                            .arrivalAirport(AirportFactory.createValidAirport("TKS"))
                            .numberOfPassenger(1)
                            .passengerNames(new String[] { "PYO HYEON" })
                            .build());

    @Bean
    Reservation reservation_flightBooking_Eastarjet_ZE671_html() {
        return reservation_flightBooking_Eastarjet_ZE671_builder
                .rawText(parseReservationText("text/flightBooking/eastarjet/gmail_html_ko.txt"))
                .build();
    }

    @Bean
    Reservation reservation_flightBooking_Eastarjet_ZE671_text() {
        return reservation_flightBooking_Eastarjet_ZE671_builder
                .rawText(parseReservationText("text/flightBooking/eastarjet/gmail_text_ko.txt"))
                .build();
    }

    @Bean
    Reservation reservation_accomodation_HostelPAQTokushima() {
        return Reservation.builder().category(ReservationCategory.ACCOMODATION)
                .rawText(parseReservationText(
                        "text/accomodation/agoda/gmail_text_ko_hostelPAQTokushima.txt"))
                .primaryHrefLink(
                        "https://agoda.onelink.me/1640755593?pid=redirect&c=CONFIRMATION_EMAIL_ONELINK&af_dp=agoda%3a%2f%2fmmb%2f%3fbookingToken%3dzPG19SiliyJJ1yAm5RPtQA%3d%3d&deep_link_value=agoda%3a%2f%2fmmb%2f%3fbookingToken%3dzPG19SiliyJJ1yAm5RPtQA%3d%3d&af_sub1=EXP-ID-AM-7093-B&af_sub3=bebf6434-a5d7-42ce-805f-3715787ae814&af_sub4=Hotel&af_force_deeplink=true&af_web_dp=https%3a%2f%2fwww.agoda.com%2faccount%2feditbooking.html%3fbookingId%3dzPG19SiliyJJ1yAm5RPtQA%3d%3d%26")
                .code("1546592100")
                .accomodation(
                        Accomodation.builder()
                                .category(AccomodationCategory.DORMITORY)
                                .title("HOSTEL PAQ tokushima")
                                .roomTitle("도미토리 내 1인 예약 (혼성)")
                                .location("Tokushima city nakatouriimachi 2-5, 도쿠시마, 일본, 770-0844")
                                .numberOfClient(1)
                                .clientName("PYO HYEON")
                                .checkinDateIsoString("2025-02-24")
                                .checkoutDateIsoString("2025-02-25")
                                .checkinStartTimeIsoString("2025-02-24T15:00")
                                .checkinEndTimeIsoString(null)
                                .checkoutTimeIsoString("2025-02-25T10:00")
                                .build())
                .build();
    }

    @Bean
    Reservation reservation_general_TeshimaArtMuseum_0() {
        return Reservation.builder().category(ReservationCategory.ACCOMODATION)
                .rawText(parseReservationText("text/general/teshima-art-museum/gmail_text_ko.txt"))
                .primaryHrefLink(
                        "https://www.etix.com/kketix/online/onlinereprint.jsp?userID=25504976&password=66755185")
                .code("4428332754")
                .generalReservation(
                        GeneralReservation.builder()
                                .title("Teshima Art Museum")
                                .numberOfClient(1)
                                .dateTimeIsoString("2025-02-22")
                                .build())
                .build();
    }

    @Bean
    Reservation reservation_general_TeshimaArtMuseum_1() {
        return Reservation.builder().category(ReservationCategory.ACCOMODATION)
                .rawText(parseReservationText("text/general/teshima-art-museum/gmail_text_ko.txt"))
                .primaryHrefLink(
                        "https://www.etix.com/kketix/online/onlinereprint.jsp?userID=25504976&password=66755185")
                .code("4428332898")
                .generalReservation(
                        GeneralReservation.builder()
                                .title("Teshima Art Museum")
                                .numberOfClient(1)
                                .dateTimeIsoString("2025-02-22")
                                .build())
                .build();
    }
}
