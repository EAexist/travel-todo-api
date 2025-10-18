package com.matchalab.trip_todo_api.controller;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.matchalab.trip_todo_api.DataLoader;
import com.matchalab.trip_todo_api.config.MockDestinationConfig;
import com.matchalab.trip_todo_api.config.TestAsyncConfig;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.event.NewTripCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.mapper.TodoMapper;
import com.matchalab.trip_todo_api.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoPresetItemDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.Todo.FlightTodoContent;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.service.TestService;
import com.matchalab.trip_todo_api.utils.TestUtils;
import com.matchalab.trip_todo_api.utils.Utils;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles({ "local" })
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ TestConfig.class, MockDestinationConfig.class, TestAsyncConfig.class })
@TestInstance(Lifecycle.PER_CLASS)
@EnableWebSecurity
@RecordApplicationEvents
public class TripControllerIntegrationTest {

    /*
     * Repository
     */
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private StockTodoContentRepository stockTodoContentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private TodoPresetRepository todoPresetRepository;

    /*
     * Mapper
     */
    @Autowired
    private TripMapper tripMapper;
    @Autowired
    private TodoMapper todoMapper;

    /*
     * TestConfig
     */

    // @Autowired
    // private TodoContent stockTodoContent;

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

    @Autowired
    private Trip trip;

    /*
     * Test Class Variables
     */
    private Trip savedTrip;

    private UserAccount userAccount;

    private UUID userAccountId;

    /*
     * Event
     */
    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /*
     * Etc
     */

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setUp() {

        tripRepository.deleteAll();
        destinationRepository.deleteAll();

        // TodoPreset todoPreset =
        // todoPresetRepository.save(TodoPreset.builder().title("기본").build());
        // todoPreset.setTodoPresetStockTodoContent(stockTodoContentRepository.findAll().stream().map(stockTodoContent
        // -> {
        // return
        // TodoPresetStockTodoContent.builder().todoPreset(null).stockTodoContent(stockTodoContent)
        // .isFlaggedToAdd(true).build();
        // }).toList());
        // todoPresetRepository.save(todoPreset);

        userAccount = userAccountRepository.save(new UserAccount());
        userAccountId = userAccount.getId();

        List<Destination> savedDestinations = List.of(new Destination(destination_kyoto),
                new Destination(destination_osaka));

        savedTrip = new Trip(trip);
        savedDestinations.stream()
                .forEach(dest -> {
                    savedTrip.getDestination().add(dest);
                    dest.getTrip().add(savedTrip);
                    destinationRepository.save(dest);
                });

        tripRepository.save(savedTrip);
        eventPublisher.publishEvent(new NewTripCreatedEvent(this, trip.getId()));

        log.info(String.format("[setUp] savedTrip=%s", Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));
    }

    @Test
    @Transactional
    void trip_Given_ValidTripId_When_RequestGet_Then_CorrectTripDTO() throws Exception {

        UUID id = savedTrip.getId();

        ResultActions result = mockMvc.perform(get(String.format("/trip/%s", id)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TripDTO responseTripDTO = TestUtils.asObject(result, TripDTO.class);
        assertThat(responseTripDTO).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields("*.id")
                .isEqualTo(tripMapper.mapToTripDTO(savedTrip));
    }

    @Test
    @Transactional
    void createTrip_When_RequestPost_Then_CreateNewTrip() throws Exception {

        ResultActions result = mockMvc.perform(post(String.format("/user/%s/trip", userAccountId)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty());

        TripDTO tripDTO = TestUtils.asObject(result, TripDTO.class);

        result.andExpect(header().string("Location",
                String.format("http://localhost/trip/%s", tripDTO.id())));
    }

    @Test
    @Transactional
    void patchTrip_Given_ValidIdAndNewContent_When_RequestPut_Then_patchTrip() throws Exception {

        TripDTO tripDTOToPatch = TripDTO.builder().isInitialized(false).title("새 여행 이름")
                .startDateIsoString("2025-02-10T00:00:00.001Z").build();

        ResultActions result = mockMvc
                .perform(patch(String.format("/trip/%s", savedTrip.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(tripDTOToPatch)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("title").value(tripDTOToPatch.title()))
                .andExpect(jsonPath("startDateIsoString").value(tripDTOToPatch.startDateIsoString()));

        TripDTO actualTripDTO = TestUtils.asObject(result, TripDTO.class);

        log.info(String.format("[patchTrip_Given_ValidIdAndNewContent_When_RequestPut_Then_patchTrip] %s",
                Utils.asJsonString(actualTripDTO)));

        log.info(String.format("[patchTrip_Given_ValidIdAndNewContent_When_RequestPut_Then_patchTrip] %s",
                Utils.asJsonString(tripMapper.mapToTripDTO(savedTrip))));

        assertThat(actualTripDTO).usingRecursiveComparison()
                // .ignoringFieldsOfTypes(TripDTO.class)
                .ignoringFields("title", "startDateIsoString")
                .isEqualTo(tripMapper.mapToTripDTO(savedTrip));
    }

    @Test
    @Transactional
    void getTodoPreset_Given_PopulatedPresetDB_When_RequestGet_Then_AllPresets() throws Exception {

        log.info(String.format("[getTodoPreset_Given_PopulatedPresetDB_When_RequestGet_Then_AllPresets] %s",
                Utils.asJsonString(stockTodoContentRepository.findAll())));

        ResultActions result = mockMvc
                .perform(get(String.format("/trip/%s/todoPreset", savedTrip.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        List<TodoPresetItemDTO> actualTodoPresetItemDTOs = TestUtils.asObject(result,
                new TypeReference<List<TodoPresetItemDTO>>() {
                });

        List<FlightRoute> recommendedOutboudFlight = savedTrip.getDestination().stream()
                .map(dest -> dest.getRecommendedOutboundFlight()).flatMap(List::stream)
                .collect(Collectors.toList());

        List<FlightRoute> recommendedReturnFlight = savedTrip.getDestination().stream()
                .map(dest -> dest.getRecommendedReturnFlight()).flatMap(List::stream)
                .collect(Collectors.toList());

        List<TodoPresetItemDTO> stockTodoPresetItemDTOs = todoPresetRepository.findByTitle("기본")
                .orElseThrow(() -> new NotFoundException(null)).getTodoPresetStockTodoContents().stream()
                .map(todoMapper::mapToTodoPresetItemDTO).toList();

        List<TodoPresetItemDTO> expectedTodoPresetItemDTOs = new ArrayList<TodoPresetItemDTO>(stockTodoPresetItemDTOs);
        // expectedTodoPresetItemDTOs.addAll(Arrays.asList(
        // new TodoPresetItemDTO(true,
        // TodoContentDTO.builder().isStock(false).category("reservation").type("flight")
        // .title("가는 항공편").icon(new Icon("✈️"))
        // .flightTodoContent(new FlightTodoContent(null,
        // recommendedOutboudFlight)).build()),

        // new TodoPresetItemDTO(true,
        // TodoContentDTO.builder().isStock(false).category("reservation").type("flight")
        // .title("오는 항공편").icon(new Icon("✈️"))
        // .flightTodoContent(new FlightTodoContent(null, recommendedReturnFlight))
        // .build())));

        assertThat(actualTodoPresetItemDTOs).usingRecursiveComparison()
                .ignoringFieldsOfTypes().ignoringFields("todoContent.id", "*.todoContent.todo")
                .isEqualTo(expectedTodoPresetItemDTOs);
    }

    @Test
    @Transactional
    void createDestination_Given_ValidTripIdAndDestinationDTO_When_RequestPost_Then_AddDestinationToTrip()
            throws Exception {

        UUID tripId = savedTrip.getId();

        ResultActions result = mockMvc
                .perform(post(String.format("/trip/%s/destination", tripId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(destinationDTO_tokushima)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id")
                        .isNotEmpty())
                .andExpect(jsonPath("title").value(destinationDTO_tokushima.title()))
                .andExpect(jsonPath("iso2DigitNationCode").value(destinationDTO_tokushima.iso2DigitNationCode()))
                .andExpect(jsonPath("region").value(destinationDTO_tokushima.region()))
                .andExpect(jsonPath("description").value(destinationDTO_tokushima.description()));

        DestinationDTO actualDestinationDTO = TestUtils.asObject(result, DestinationDTO.class);

        result.andExpect(header().string("Location",
                String.format("http://localhost/destination/%s", actualDestinationDTO.id(),
                        actualDestinationDTO.id())));
    }

    @Test
    @Transactional
    void createDestination_Given_AlreadyExistingDestination_Then_DoNotCreateRedundantDestination() throws Exception {

        UUID destinationId_osaka = destinationRepository.findByiso2DigitNationCodeAndTitle("JP", "오사카")
                .orElseThrow(() -> new NotFoundException(null)).getId();

        ResultActions result = mockMvc
                .perform(post(String.format("/trip/%s/destination", savedTrip.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.asJsonString(destinationDTO_osaka)))
                .andDo(print());

        DestinationDTO actualDestinationDTO = TestUtils.asObject(result, DestinationDTO.class);

        assertEquals(destinationId_osaka, actualDestinationDTO.id());
        assertThat(applicationEvents.stream().anyMatch(event -> event instanceof NewDestinationCreatedEvent)).isFalse();
        assertThat(applicationEvents.stream().anyMatch(event -> event instanceof NewFlightRouteCreatedEvent)).isFalse();
    }

    @Transactional
    private Destination findDestinationWithRecommendedFlights(UUID destinationId) {
        Destination destination = destinationRepository.findById(destinationId).orElseThrow();
        destination.getRecommendedOutboundFlight().size();
        destination.getRecommendedReturnFlight().size();
        // Hibernate.initialize(destination.getRecommendedOutboundFlight());
        // Hibernate.initialize(destination.getRecommendedReturnFlight());
        return destination;
    }

    @Test
    @Transactional
    void createDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_AddFlightRouteAndAirlines()
            throws Exception {

        // ResultActions result = mockMvc
        // .perform(post(String.format("/trip/%s/destination",
        // savedTrip.getId()))
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(Utils.asJsonString(destinationDTO_tokushima)))
        // .andDo(print());

        // String destinationId = TestUtils.asObject(result, DestinationDTO.class).id();

        // Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> {
        // Destination destination = testService.findDestinationById(destinationId);
        // return destination.getRecommendedOutboundFlight().size() > 0;
        // });

        // FlightRoute bestFlightRouteResult =
        // destination.getRecommendedOutboundFlight().getFirst();

        // assertThat(bestFlightRouteResult)
        // .usingRecursiveComparison()
        // .comparingOnlyFields("IataCode")
        // // .ignoringFieldsOfTypes(FlightRoute.class, Airport.class)
        // .isEqualTo(List.of(new FlightRoute(AirportFactory.createValidAirport("ICN"),
        // AirportFactory.createValidAirport("KIX"))));

        // assertThat(bestFlightRouteResult.getAirlines().stream().map(al ->
        // al.getName()).toList()).contains(
        // "이스타항공");
    }

    // @Test
    // @Transactional
    // @Transactional
    // void
    // createDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_PublishNewFlightRouteCreatedEvent()
    // throws Exception {

    // ResultActions result = mockMvc
    // .perform(post(String.format("/trip/%s/destination",
    // savedTrip.getId()))
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(Utils.asJsonString(destinationDTO_tokushima)))
    // .andDo(print());

    // assertEquals(1,
    // applicationEvents.stream(NewFlightRouteCreatedEvent.class).count());

    // }

    /* @TODO */
    // @Test
    // @Transactional
    void deleteDestination_When_Then() throws Exception {
    }

    // @Test
    // @Transactional
    // void
    // accomodationPlan_Given_TripWithAccomodation_When_RequestGet_Then_AllAccomodations()
    // throws Exception {

    // String id = savedTrip.getId();

    // ResultActions result =
    // mockMvc.perform(get(String.format("/trip/%s/accomodation",
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

    // @Test
    // @Transactional
    // void createAccomodation_When_RequestPost_Then_CreateNewAccomodation() throws
    // Exception {

    // String id = savedTrip.getId();

    // ResultActions result =
    // mockMvc.perform(post(String.format("/trip/%s/accomodation",
    // userAccountId, id))
    // .contentType(MediaType.APPLICATION_JSON))
    // .andDo(print())
    // .andExpect(status().isCreated())
    // .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    // .andExpect(jsonPath("id").isNotEmpty());

    // Accomodation createdAccomodation = TestUtils.asObject(result,
    // Accomodation.class);
    // result.andExpect(header().string("Location",
    // String.format("http://localhost/trip/%s/accomodation/%s",
    // userAccountId, id,
    // createdAccomodation.getId())));
    // }
}
