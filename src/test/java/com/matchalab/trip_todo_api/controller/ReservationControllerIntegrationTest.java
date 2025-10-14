package com.matchalab.trip_todo_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.matchalab.trip_todo_api.config.MockReservationConfig;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.CreateReservationDTO;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.service.HtmlParserService;
import com.matchalab.trip_todo_api.utils.TestUtils;
import com.matchalab.trip_todo_api.utils.Utils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/* 
 * @Dependency HtmlParserService
 */
@Slf4j
@ActiveProfiles({ "local", "local-init-data" })
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@Import({ TestConfig.class, MockReservationConfig.class })
@EnableWebSecurity
public class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private HtmlParserService htmlParserService;

    @Autowired
    private Trip trip;

    @Autowired
    private Reservation reservation_flightBooking_Eastarjet_ZE671_html;

    @Autowired
    private Reservation reservation_flightBooking_Eastarjet_ZE671_text;

    @Autowired
    private Reservation reservation_flightTicket_Eastarjet_ZE671;

    @Autowired
    private Reservation reservation_accomodation_HostelPAQTokushima;

    private UUID userAccountId;

    private UUID tripId;

    @BeforeAll
    void setUp() {
        tripRepository.deleteAll();

        userAccountId = userAccountRepository.save(new UserAccount()).getId();

        trip = new Trip(trip);
        Trip savedTrip = tripRepository.save(trip);
        tripId = savedTrip.getId();

        log.info(String.format("[setUp] savedTrip=%s", Utils.asJsonString(savedTrip)));

    }

    /* EastarJet */
    /* Booking */
    @Test
    @Transactional
    void createReservationFromText_Given_FlightBooking_Eastarjet_Gmail_Rtf_Ko_When_RequestPostWithoutCategory_Then_createReservation()
            throws Exception {

        List<Reservation> reservation = postReservationAndTestLocation(
                "text/flightBooking/eastarjet/gmail_html_ko.txt");

        assertThat(reservation).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id")
                .isEqualTo(
                        List.of(reservation_flightBooking_Eastarjet_ZE671_html));
    }

    @Test
    @Transactional
    void createReservationFromText_Given_FlightBooking_Eastarjet_Gmail_Text_Ko_When_RequestPostWithoutCategory_Then_createReservation()
            throws Exception {

        List<Reservation> reservation = postReservationAndTestLocation(
                "text/flightBooking/eastarjet/gmail_text_ko.txt");

        assertThat(reservation).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id")
                .isEqualTo(
                        List.of(reservation_flightBooking_Eastarjet_ZE671_text));
    }

    /* Ticket */
    @Test
    @Transactional
    void createReservationFromText_Given_FlightTicket_Eastarjet_Kakao_Text_Ko_When_RequestPostWithoutCategory_Then_createReservation()
            throws Exception {

        List<Reservation> reservation = postReservationAndTestLocation(
                "text/flightTicket/eastarjet/kakao_text_ko.txt");

        assertThat(reservation)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id")
                .ignoringFields("flightTicket.todo")
                .isEqualTo(
                        List.of(reservation_flightTicket_Eastarjet_ZE671));
    }

    /* Agoda */
    @Test
    @Transactional
    void createReservationFromText_Given_AgodaKoGmailRtf_When_RequestPostWithoutCategory_Then_createReservation()
            throws Exception {

        List<Reservation> reservation = postReservationAndTestLocation(
                "text/accomodation/agoda/gmail_html_ko_hostelPAQTokushima.txt");

        assertThat(reservation)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id")
                .isEqualTo(
                        List.of(reservation_accomodation_HostelPAQTokushima));
    }

    @Test
    @Transactional
    void createReservationFromText_Given_AgodaKoGmailText_When_RequestPostWithoutCategory_Then_createReservation()
            throws Exception {

        List<Reservation> reservation = postReservationAndTestLocation(
                "text/accomodation/agoda/gmail_text_ko_hostelPAQTokushima.txt");

        assertThat(reservation)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id")
                .isEqualTo(
                        List.of(reservation_accomodation_HostelPAQTokushima));
    }

    private List<Reservation> postReservationAndTestLocation(String resourcePath) throws Exception {

        ClassPathResource resource_gmail_agoda = new ClassPathResource(resourcePath);
        String confirmationText = StreamUtils.copyToString(resource_gmail_agoda.getInputStream(),
                StandardCharsets.UTF_8);

        CreateReservationDTO createReservationDTO = CreateReservationDTO.builder()
                .category(ReservationCategory.UNKNOWN.name())
                // .category(ReservationCategory.ACCOMODATION.name())
                .confirmationText(confirmationText).build();

        ResultActions result = mockMvc
                .perform(post(String.format("/user/%s/trip/%s/reservation", userAccountId, tripId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(createReservationDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        List<Reservation> reservation = TestUtils.asObject(result, new TypeReference<List<Reservation>>() {
        });

        result.andExpect(header().string("Location",
                String.format("http://localhost/user/%s/trip/%s/reservation/%s",
                        userAccountId,
                        tripId, reservation.getFirst().getId())));

        assertThat(reservation.getFirst().getRawText())
                .isEqualTo(htmlParserService.extractTextAndLink(confirmationText));

        return reservation;
    }

    // @Test
    // @Transactional
    // void testExtractTextFromImage() throws Exception {

    // String id = savedTrip.getId();
    // File _file = new
    // ClassPathResource("/image/accomodation-agoda-app-ios_1.png").getFile();
    // MultipartFile file = new MockMultipartFile(_file.getName(), new
    // FileInputStream(_file));

    // ResultActions result = mockMvc
    // .perform(post(String.format("/user/%s/trip/%s/reservation", userAccountId,
    // id))
    // .contentType(MediaType.APPLICATION_JSON)
    // // .content(Utils.asJsonString(new MultipartFile[] { file
    // // }))
    // )
    // .andDo(print())
    // .andExpect(status().isOk())
    // .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    // }
}
