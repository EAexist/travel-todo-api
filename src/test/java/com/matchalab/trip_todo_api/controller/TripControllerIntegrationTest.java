package com.matchalab.trip_todo_api.controller;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.matchalab.trip_todo_api.DataLoader;
import com.matchalab.trip_todo_api.config.RecommendedFlightTestConfig;
import com.matchalab.trip_todo_api.config.TestAsyncConfig;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.factory.AirportFactory;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.model.DTO.PresetDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.Todo.PresetTodoContent;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.PresetTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.service.TestService;
import com.matchalab.trip_todo_api.utils.TestUtils;
import com.matchalab.trip_todo_api.utils.Utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ TestConfig.class, RecommendedFlightTestConfig.class, TestAsyncConfig.class })
// @TestPropertySource(properties = { "spring.config.location =
// classpath:application-test.yml" })
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("local")
@EnableWebSecurity
@RecordApplicationEvents
public class TripControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private PresetTodoContentRepository presetTodoContentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private TripMapper tripMapper;

    /*
     * TestConfig
     */

    @Autowired
    private PresetTodoContent presetTodoContent;

    @Autowired
    private Accomodation[] accomodations;

    @Autowired
    private Destination destination_kyoto;

    @Autowired
    private Destination destination_osaka;

    @Autowired
    private DestinationDTO destinationDTO_osaka;

    @Autowired
    private Destination destination_tokushima;

    @Autowired
    private DestinationDTO destinationDTO_tokushima;

    @Autowired
    private Airport IncheonIntl;

    @Autowired
    private Airport TokushimaIntl;

    /*
     * Test Class Variables
     */
    private Trip savedTrip;

    private Long userAccountId;

    /*
     * Event
     */
    @Autowired
    private ApplicationEvents applicationEvents;

    /*
     * Etc
     */
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestService testService;

    @BeforeAll
    void setUp() {
        tripRepository.deleteAll();
        destinationRepository.deleteAll();

        userAccountId = userAccountRepository.save(new UserAccount()).getId();

        List<Destination> savedDestinations = List.of(new Destination(destination_kyoto),
                new Destination(destination_osaka));

        savedTrip = new Trip();
        savedDestinations.stream()
                .forEach(dest -> {
                    savedTrip.getDestination().add(dest);
                    destinationRepository.save(dest);
                });
        savedTrip.setAccomodation(Arrays.stream(accomodations)
                .map(acc -> {
                    Accomodation newAcc = new Accomodation(acc);
                    return newAcc;
                })
                .toList());
        tripRepository.save(savedTrip);
        log.info(String.format("[setUp] savedTrip=%s", Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));
    }

    @Test
    void testExtractTextFromImage() throws Exception {

        Long id = savedTrip.getId();
        File _file = new ClassPathResource("/image/accomodation-agoda-app-ios_1.png").getFile();
        MultipartFile file = new MockMultipartFile(_file.getName(), new FileInputStream(_file));

        ResultActions result = mockMvc
                .perform(post(String.format("/user/%s/trip/%s/reservation", userAccountId, id))
                        .contentType(MediaType.APPLICATION_JSON)
                // .content(Utils.asJsonString(new MultipartFile[] { file
                // }))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void trip_Given_ValidTripId_When_RequestGet_Then_CorrectTripDTO() throws Exception {

        Long id = savedTrip.getId();

        ResultActions result = mockMvc.perform(get(String.format("/user/%s/trip/%s", userAccountId, id)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TripDTO responseTripDTO = TestUtils.asObject(result, TripDTO.class);
        assertThat(responseTripDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields("*.id")
                .isEqualTo(tripMapper.mapToTripDTO(savedTrip));
        /*
         * https://stackoverflow.com/questions/24927086/understanding-transactions-in-
         * spring-test
         */
    }

    @Test
    void createTrip_When_RequestPost_Then_CreateNewTrip() throws Exception {

        ResultActions result = mockMvc.perform(post(String.format("/user/%s/trip", userAccountId)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty());

        Long createdTripid = TestUtils.asObject(result, TripDTO.class).id();
        result.andExpect(header().string("Location",
                String.format("http://localhost/user/%s/trip/%s", userAccountId, createdTripid)));

    }

    @Test
    void patchTrip_Given_ValidIdAndNewContent_When_RequestPut_Then_patchTrip() throws Exception {

        TripDTO tripDTOToPatch = new TripDTO(null,
                false,
                "새 여행 이름",
                "2025-02-10T00:00:00.001Z",
                null,
                null,
                null,
                null);

        ResultActions result = mockMvc
                .perform(patch(String.format("/user/%s/trip/%s", userAccountId, savedTrip.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(tripDTOToPatch)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("title").value(tripDTOToPatch.title()))
                .andExpect(jsonPath("startDateISOString").value(tripDTOToPatch.startDateISOString()));

        TripDTO actualTripDTO = TestUtils.asObject(result, TripDTO.class);

        log.info(String.format("[patchTrip_Given_ValidIdAndNewContent_When_RequestPut_Then_patchTrip] %s",
                Utils.asJsonString(actualTripDTO)));

        log.info(String.format("[patchTrip_Given_ValidIdAndNewContent_When_RequestPut_Then_patchTrip] %s",
                Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));

        assertThat(actualTripDTO).usingRecursiveComparison()
                // .ignoringFieldsOfTypes(TripDTO.class)
                .ignoringFields("title", "startDateISOString")
                .isEqualTo(tripMapper.mapToTripDTO(savedTrip));

        /*
         * https://stackoverflow.com/questions/24927086/understanding-transactions-in-
         * spring-test
         */
        // .andExpect(jsonPath("destination").value(trip.getDestination()))
        // .andExpect(jsonPath("todolist").value(trip.getTriplist()))
        // .andExpect(jsonPath("accomodation").value(trip.getAccomodation()));
    }

    @Test
    void getTodoPreset_Given_PopulatedPresetDB_When_RequestGet_Then_AllPresets() throws Exception {

        log.info(String.format("[getTodoPreset_Given_PopulatedPresetDB_When_RequestGet_Then_AllPresets] %s",
                Utils.asJsonString(presetTodoContentRepository.findAll())));

        ResultActions result = mockMvc.perform(get(String.format("/user/%s/trip/0/todoPreset", userAccountId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        List<PresetDTO> actualPresetDTO = TestUtils.asObject(result,
                new TypeReference<List<PresetDTO>>() {
                });

        List<PresetDTO> expectedPresetDTO = DataLoader.readPresetJson().stream()
                .map(presetTodoContent -> new PresetDTO(true, presetTodoContent)).toList();

        assertThat(actualPresetDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields()
                .isEqualTo(expectedPresetDTO);
    }

    @Test
    void createDestination_Given_ValidTripIdAndDestinationDTO_When_RequestPost_Then_AddDestinationToTrip()
            throws Exception {

        Long tripId = savedTrip.getId();

        ResultActions result = mockMvc
                .perform(post(String.format("/user/%s/trip/%s/destination", userAccountId, tripId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(destinationDTO_tokushima)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id")
                        .isNotEmpty())
                .andExpect(jsonPath("title").value(destinationDTO_tokushima.title()))
                .andExpect(jsonPath("countryISO").value(destinationDTO_tokushima.countryISO()))
                .andExpect(jsonPath("region").value(destinationDTO_tokushima.region()))
                .andExpect(jsonPath("description").value(destinationDTO_tokushima.description()));

        DestinationDTO actualDestinationDTO = TestUtils.asObject(result, DestinationDTO.class);

        result.andExpect(header().string("Location",
                String.format("http://localhost/user/%s/trip/%s/destination/%s", userAccountId, tripId,
                        actualDestinationDTO.id())));
    }

    @Test
    void createDestination_Given_AlreadyExistingDestination_Then_DoNotCreateRedundantDestination() throws Exception {

        Long destinationId_osaka = destinationRepository.findByCountryISOAndTitle("JP", "오사카")
                .orElseThrow(() -> new NotFoundException(null)).getId();

        ResultActions result = mockMvc
                .perform(post(String.format("/user/%s/trip/%s/destination", userAccountId, savedTrip.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(destinationDTO_osaka)))
                .andDo(print());

        DestinationDTO actualDestinationDTO = TestUtils.asObject(result, DestinationDTO.class);

        assertEquals(destinationId_osaka, actualDestinationDTO.id());
        assertThat(applicationEvents.stream().anyMatch(event -> event instanceof NewDestinationCreatedEvent)).isFalse();
        assertThat(applicationEvents.stream().anyMatch(event -> event instanceof NewFlightRouteCreatedEvent)).isFalse();
    }

    @Transactional
    private Destination findDestinationWithRecommendedFlights(Long destinationId) {
        Destination destination = destinationRepository.findById(destinationId).orElseThrow();
        destination.getRecommendedOutboundFlight().size();
        destination.getRecommendedReturnFlight().size();
        // Hibernate.initialize(destination.getRecommendedOutboundFlight());
        // Hibernate.initialize(destination.getRecommendedReturnFlight());
        return destination;
    }

    @Test
    void createDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_AddFlightRouteAndAirlines()
            throws Exception {

        // ResultActions result = mockMvc
        // .perform(post(String.format("/user/%s/trip/%s/destination", userAccountId,
        // savedTrip.getId()))
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(Utils.asJsonString(destinationDTO_tokushima)))
        // .andDo(print());

        // Long destinationId = TestUtils.asObject(result, DestinationDTO.class).id();

        // Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> {
        // Destination destination = testService.findDestinationById(destinationId);
        // return destination.getRecommendedOutboundFlight().size() > 0;
        // });

        // FlightRoute bestFlightRouteResult =
        // destination.getRecommendedOutboundFlight().getFirst();

        // assertThat(bestFlightRouteResult)
        // .usingRecursiveComparison()
        // .comparingOnlyFields("IATACode")
        // // .ignoringFieldsOfTypes(FlightRoute.class, Airport.class)
        // .isEqualTo(List.of(new FlightRoute(AirportFactory.createValidAirport("ICN"),
        // AirportFactory.createValidAirport("KIX"))));

        // assertThat(bestFlightRouteResult.getAirlines().stream().map(al ->
        // al.getName()).toList()).contains(
        // "이스타항공");
    }

    // @Test
    // @Transactional
    // void
    // createDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_PublishNewFlightRouteCreatedEvent()
    // throws Exception {

    // ResultActions result = mockMvc
    // .perform(post(String.format("/user/%s/trip/%s/destination", userAccountId,
    // savedTrip.getId()))
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(Utils.asJsonString(destinationDTO_tokushima)))
    // .andDo(print());

    // assertEquals(1,
    // applicationEvents.stream(NewFlightRouteCreatedEvent.class).count());

    // }

    /* @TODO */
    // @Test
    void deleteDestination_When_Then() throws Exception {
    }

    // @Test
    // void
    // accomodationPlan_Given_TripWithAccomodation_When_RequestGet_Then_AllAccomodations()
    // throws Exception {

    // Long id = savedTrip.getId();

    // ResultActions result =
    // mockMvc.perform(get(String.format("/user/%s/trip/%s/accomodation",
    // userAccountId,id)))
    // .andDo(print())
    // .andExpect(status().isOk())
    // .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    // .andExpect(jsonPath("$[0].id").isNotEmpty())
    // .andExpect(jsonPath("$[1].id").isNotEmpty());

    // List<AccomodationDTO> actualAccomodationDTOs = TestUtils.asObject(result, new
    // TypeReference<List<AccomodationDTO>>() {
    // });

    // List<AccomodationDTO> expectedDTO =
    // Arrays.stream(accomodations).map(tripMapper::mapToAccomodationDTO).toList();

    // assertThat(actualAccomodationDTOs).usingRecursiveComparison()
    // .ignoringFieldsOfTypes().ignoringFields(".*id")
    // .isEqualTo(expectedDTO);

    // }

    @Test
    void createAccomodation_When_RequestPost_Then_CreateNewAccomodation() throws Exception {

        Long id = savedTrip.getId();

        ResultActions result = mockMvc.perform(post(String.format("/user/%s/trip/%s/accomodation", userAccountId, id))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty());

        Accomodation createdAccomodation = TestUtils.asObject(result, Accomodation.class);
        result.andExpect(header().string("Location",
                String.format("http://localhost/user/%s/trip/%s/accomodation/%s", userAccountId, id,
                        createdAccomodation.getId())));
    }

    /* @TODO */
    // @Test
    void deleteAccomodation_When_Then() throws Exception {
    }
}
