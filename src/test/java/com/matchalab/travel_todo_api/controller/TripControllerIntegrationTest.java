package com.matchalab.travel_todo_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.matchalab.travel_todo_api.DTO.*;
import com.matchalab.travel_todo_api.config.MockDestinationConfig;
import com.matchalab.travel_todo_api.config.TestAsyncConfig;
import com.matchalab.travel_todo_api.config.TestConfig;
import com.matchalab.travel_todo_api.enums.TodoCategory;
import com.matchalab.travel_todo_api.enums.TodoPresetType;
import com.matchalab.travel_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.travel_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.travel_todo_api.exception.NotFoundException;
import com.matchalab.travel_todo_api.mapper.TodoMapper;
import com.matchalab.travel_todo_api.model.Destination;
import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import com.matchalab.travel_todo_api.model.Icon;
import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;
import com.matchalab.travel_todo_api.model.Todo.Todo;
import com.matchalab.travel_todo_api.model.Todo.TodoPreset;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.repository.*;
import com.matchalab.travel_todo_api.utils.TestUtils;
import com.matchalab.travel_todo_api.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@WithMockUser
@Import({TestConfig.class, MockDestinationConfig.class, TestAsyncConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
@EnableWebSecurity
@RecordApplicationEvents
@SpringBootTest
public class TripControllerIntegrationTest {

  /*
   * Repository
   */
  @Autowired private TripRepository tripRepository;

  @Autowired private StockTodoContentRepository stockTodoContentRepository;

  @Autowired private UserAccountRepository userAccountRepository;

  @Autowired private DestinationRepository destinationRepository;

  @Autowired private TodoPresetRepository todoPresetRepository;

  /*
   * Mapper
   */
  @Autowired private TodoMapper todoMapper;

  /*
   * TestConfig
   */
  @Autowired private DestinationDTO destinationDTO_tokushima;

  @Autowired private DestinationDTO destinationDTO_osaka;

  @Autowired private Trip trip;

  @Autowired private TripDTO tripDto;

  @Autowired private Destination[] destinations;

  @Autowired private Todo stockTodo;

  @Autowired private Todo customTodo;

  /*
   * Test Class Variables
   */
  private Trip savedTrip;

  private UserAccount userAccount;

  private UUID userAccountId;

  /*
   * Event
   */
  @Autowired private ApplicationEvents applicationEvents;

  @Autowired private ApplicationEventPublisher eventPublisher;

  /*
   * Etc
   */
  @Autowired private MockMvc mockMvc;

  @BeforeAll
  void setUp() {

    tripRepository.deleteAll();
    destinationRepository.deleteAll();

    userAccount = userAccountRepository.save(new UserAccount());
    userAccountId = userAccount.getId();

    List<Destination> savedDestinations = destinationRepository.saveAll(List.of(destinations));

    savedTrip = tripRepository.save(new Trip(trip));

    savedTrip.addDestinations(savedDestinations);

    TodoPreset preset =
        todoPresetRepository
            .findByType(TodoPresetType.DEFAULT)
            .orElseThrow(() -> new NotFoundException(null));
    savedTrip.setTodoPreset(preset);

    StockTodoContent stockTodoContent =
        stockTodoContentRepository
            .findByType("CASH")
            .orElseThrow(() -> new NotFoundException(null));
    ;
    stockTodo.setStockTodoContent(stockTodoContent);
    savedTrip.addTodo(stockTodo);

    savedTrip.addTodo(customTodo);

    tripRepository.save(savedTrip);
  }

  @Test
  @Transactional
  void givenValidTripId_whenGetTrip_thenReturnsCorrectTripDto() throws Exception {

    UUID id = savedTrip.getId();

    ResultActions result =
        mockMvc
            .perform(get(String.format("/trip/%s", id)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    TripDTO responseTripDTO = TestUtils.asObject(result, TripDTO.class);
    assertThat(responseTripDTO)
        .usingRecursiveComparison()
        .ignoringFields("stockTodoContents")
        .ignoringFieldsMatchingRegexes(".*\\.id")
        .ignoringFieldsOfTypes(UUID.class)
        .isEqualTo(tripDto);
  }

  @Test
  @Transactional
  void whenCreateTrip_thenReturnsCreated() throws Exception {

    ResultActions result =
        mockMvc
            .perform(post(String.format("/user/%s/trip", userAccountId)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id").isNotEmpty());

    TripDTO tripDTO = TestUtils.asObject(result, TripDTO.class);

    result.andExpect(
        header().string("Location", String.format("http://localhost/trip/%s", tripDTO.id())));
  }

  @Test
  @Transactional
  void givenValidPatchDto_whenPatchTrip_thenReturnsOk() throws Exception {

    TripPatchDTO tripDTOToPatch =
        TripPatchDTO.builder()
            .isInitialized(false)
            .title("새 여행 이름")
            .startDateIsoString("2025-02-10T00:00:00.001Z")
            .build();

    ResultActions result =
        mockMvc
            .perform(
                patch(String.format("/trip/%s", savedTrip.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Utils.asJsonString(tripDTOToPatch)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("title").value(tripDTOToPatch.title()))
            .andExpect(jsonPath("startDateIsoString").value(tripDTOToPatch.startDateIsoString()));

    TripDTO actualTripDTO = TestUtils.asObject(result, TripDTO.class);

    assertThat(actualTripDTO)
        .usingRecursiveComparison()
        // .ignoringFieldsOfTypes(TripDTO.class)
        .ignoringFields("id", "title", "startDateIsoString", "stockTodoContents")
        .ignoringFieldsMatchingRegexes(".*\\.id")
        .isEqualTo(tripDto);
  }

  @Test
  @Transactional
  void givenPopulatedPresetDb_whenGetTodoPresets_thenReturnsAllPresets() throws Exception {

    ResultActions result =
        mockMvc
            .perform(get(String.format("/trip/%s/todoPreset", savedTrip.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    List<TodoPresetItemDTO> actualTodoPresetItemDTOs =
        TestUtils.asObject(result, new TypeReference<List<TodoPresetItemDTO>>() {});

    List<FlightRoute> recommendedOutboudFlight =
        savedTrip.getDestinationsDirectly().stream()
            .map(Destination::getRecommendedOutboundFlight)
            .flatMap(List::stream)
            .toList();

    List<FlightRoute> recommendedReturnFlight =
        savedTrip.getDestinationsDirectly().stream()
            .map(Destination::getRecommendedReturnFlight)
            .flatMap(List::stream)
            .toList();

    List<TodoPresetItemDTO> stockTodoPresetItemDTOs =
        todoPresetRepository
            .findByType(TodoPresetType.JAPAN)
            .orElseThrow(() -> new NotFoundException(null))
            .getTodoPresetStockTodoContents()
            .stream()
            .map(todoMapper::mapToTodoPresetItemDTO)
            .toList();

    List<TodoPresetItemDTO> expectedTodoPresetItemDTOs =
        new ArrayList<TodoPresetItemDTO>(stockTodoPresetItemDTOs);
    expectedTodoPresetItemDTOs.addAll(
        Arrays.asList(
            new TodoPresetItemDTO(
                true,
                TodoContentDTO.builder()
                    .id(UUID.nameUUIDFromBytes("outbound-flight".getBytes()))
                    .isStock(false)
                    .category(TodoCategory.RESERVATION)
                    .type("FLIGHT_OUTBOUND")
                    .title("항공권 구매")
                    .icon(new Icon("🛫"))
                    .subtitle("출발 비행기")
                    .build()),
            new TodoPresetItemDTO(
                true,
                TodoContentDTO.builder()
                    .id(UUID.nameUUIDFromBytes("return-flight".getBytes()))
                    .isStock(false)
                    .category(TodoCategory.RESERVATION)
                    .type("FLIGHT_RETURN")
                    .title("항공권 구매")
                    .icon(new Icon("🛬"))
                    .subtitle("돌아오는 비행기")
                    .build())));

    assertThat(actualTodoPresetItemDTOs)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFieldsOfTypes()
        .ignoringFields("*.content.flightRoutes")
        .isEqualTo(expectedTodoPresetItemDTOs);
  }

  @Test
  @Transactional
  void givenValidDestinationDto_whenAddDestination_thenReturnsCreated() throws Exception {

    UUID tripId = savedTrip.getId();

    ResultActions result =
        mockMvc
            .perform(
                post(String.format("/trip/%s/destination", tripId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Utils.asJsonString(destinationDTO_osaka)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id").isNotEmpty())
            .andExpect(jsonPath("title").value(destinationDTO_osaka.title()))
            .andExpect(
                jsonPath("iso2DigitNationCode").value(destinationDTO_osaka.iso2DigitNationCode()))
            .andExpect(jsonPath("region").value(destinationDTO_osaka.region()))
            .andExpect(jsonPath("description").value(destinationDTO_osaka.description()));

    DestinationDTO actualDestinationDTO = TestUtils.asObject(result, DestinationDTO.class);

    result.andExpect(
        header()
            .string(
                "Location",
                String.format("http://localhost/destination/%s", actualDestinationDTO.id())));
  }

  @Test
  @Transactional
  void givenExistingDestination_whenAddDestination_thenReturnsExistingId() throws Exception {

    UUID destinationId_tokushima =
        destinationRepository
            .findByiso2DigitNationCodeAndTitle("JP", "도쿠시마")
            .orElseThrow(() -> new NotFoundException(null))
            .getId();

    ResultActions result =
        mockMvc
            .perform(
                post(String.format("/trip/%s/destination", savedTrip.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Utils.asJsonString(destinationDTO_tokushima)))
            .andDo(print());

    DestinationDTO actualDestinationDTO = TestUtils.asObject(result, DestinationDTO.class);

    assertEquals(destinationId_tokushima, actualDestinationDTO.id());
    assertThat(
            applicationEvents.stream()
                .anyMatch(event -> event instanceof NewDestinationCreatedEvent))
        .isFalse();
    assertThat(
            applicationEvents.stream()
                .anyMatch(event -> event instanceof NewFlightRouteCreatedEvent))
        .isFalse();
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
  void
      addDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_AddFlightRouteAndAirlines()
          throws Exception {

    // ResultActions result = mockMvc
    // .perform(post(String.format("/trip/%s/destination",
    // savedTrip.getId()))
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(Utils.asJsonString(destinationDTO_osaka)))
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
  // addDestination_Given_FirstSeenDestinationWithFirstSeenFlightRoute_When_RequestPost_Then_PublishNewFlightRouteCreatedEvent()
  // throws Exception {

  // ResultActions result = mockMvc
  // .perform(post(String.format("/trip/%s/destination",
  // savedTrip.getId()))
  // .contentType(MediaType.APPLICATION_JSON)
  // .content(Utils.asJsonString(destinationDTO_osaka)))
  // .andDo(print());

  // assertEquals(1,
  // applicationEvents.stream(NewFlightRouteCreatedEvent.class).count());

  // }
}
