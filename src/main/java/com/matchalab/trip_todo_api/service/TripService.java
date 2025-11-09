package com.matchalab.trip_todo_api.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchalab.trip_todo_api.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.DTO.FlightRouteDTO;
import com.matchalab.trip_todo_api.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.DTO.TodoPresetItemDTO;
import com.matchalab.trip_todo_api.DTO.TripDTO;
import com.matchalab.trip_todo_api.DTO.TripPatchDTO;
import com.matchalab.trip_todo_api.config.ResourceQuota;
import com.matchalab.trip_todo_api.enums.TodoCategory;
import com.matchalab.trip_todo_api.enums.TodoPresetType;
import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.exception.TripNotFoundException;
import com.matchalab.trip_todo_api.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.mapper.TodoMapper;
import com.matchalab.trip_todo_api.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.TripDestination;
import com.matchalab.trip_todo_api.model.TripDestinationId;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetRepository;
import com.matchalab.trip_todo_api.repository.TripDestinationRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TripService {
    @Autowired
    private final UserAccountRepository userAccountRepository;
    @Autowired
    private final TripRepository tripRepository;
    @Autowired
    private final DestinationRepository destinationRepository;
    @Autowired
    private final TodoPresetRepository todoPresetRepository;
    @Autowired
    private final TripDestinationRepository tripDestinationRepository;

    @Autowired
    private final TripMapper tripMapper;

    @Autowired
    private final TodoMapper todoMapper;

    @Autowired
    private final FlightRouteMapper flightRouteMapper;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    ResourceQuota resourceQuota;

    // @Value("${app.demo.sample-trip-uuid-source-btyes}")
    // private String sampleTripUuidSourceBytes;

    // private UUID getSampleTripId() {
    // return UUID.nameUUIDFromBytes(sampleTripUuidSourceBytes.getBytes());
    // }

    public TripService(UserAccountRepository userAccountRepository,
            TripRepository tripRepository,
            DestinationRepository destinationRepository,
            TodoPresetRepository todoPresetRepository,
            TripDestinationRepository tripDestinationRepository,
            TripMapper tripMapper,
            TodoMapper todoMapper,
            FlightRouteMapper flightRouteMapper,
            ApplicationEventPublisher eventPublisher) {
        this.userAccountRepository = userAccountRepository;
        this.tripRepository = tripRepository;
        this.destinationRepository = destinationRepository;
        this.todoPresetRepository = todoPresetRepository;
        this.tripDestinationRepository = tripDestinationRepository;
        this.tripMapper = tripMapper;
        this.todoMapper = todoMapper;
        this.flightRouteMapper = flightRouteMapper;
        this.eventPublisher = eventPublisher;
    }

    public TripDTO createTrip(UUID userId) {

        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId));

        if (userAccount.getTrips().size() >= resourceQuota.getMaxTrips()) {
            while (userAccount.getTrips().size() >= resourceQuota.getMaxTrips()) {
                userAccount.getTrips().removeFirst();
            }
        }
        Trip trip = new Trip();

        /* Link TodoPreset */
        TodoPreset preset = todoPresetRepository.findByType(TodoPresetType.DEFAULT)
                .orElseThrow(() -> new NotFoundException(null));
        trip.setTodoPreset(preset);
        trip = tripRepository.save(trip);
        userAccount.addTrip(trip);
        userAccountRepository.save(userAccount);
        return tripMapper.mapToTripDTO(trip);
    }

    public TripDTO createSampleTrip(UUID userId) {

        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId));

        if (userAccount.getTrips().size() >= resourceQuota.getMaxTrips()) {
            while (userAccount.getTrips().size() >= resourceQuota.getMaxTrips()) {
                userAccount.getTrips().removeFirst();
            }
        }

        Trip sampleTrip = tripRepository.findTop1ByIsSampleTrue()
                .orElseThrow(() -> new NotFoundException(null)); // Fetch
        // Sample
        // Trip

        Trip trip = new Trip(sampleTrip); // Deep Copy
        trip = tripRepository.save(trip);
        userAccount.addTrip(trip);
        userAccountRepository.save(userAccount);
        return tripMapper.mapToTripDTO(trip);
    }

    public TripDTO createAdminSampleTrip(UUID userId) {

        if (tripRepository.findTop1ByIsSampleTrue().isPresent()) {
            return null;
        }

        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId));

        if (userAccount.getTrips().size() >= resourceQuota.getMaxTrips()) {
            while (userAccount.getTrips().size() >= resourceQuota.getMaxTrips()) {
                userAccount.getTrips().removeFirst();
            }
        }
        Trip trip = new Trip();

        // Link TodoPreset
        TodoPreset preset = todoPresetRepository.findByType(TodoPresetType.DEFAULT)
                .orElseThrow(() -> new NotFoundException(null));
        trip.setTodoPreset(preset);

        // Set Sample
        trip.setIsSample(true);
        trip = tripRepository.save(trip);

        userAccount.addTrip(trip);
        userAccountRepository.save(userAccount);

        return tripMapper.mapToTripDTO(trip);
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    public TripDTO getTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        return tripMapper.mapToTripDTO(trip);
    }

    /*
     * Get list of all destinations of a trip.
     */

    public List<DestinationDTO> getDestinations(UUID tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        return trip.getDestinationsDirectly().stream().map(tripMapper::mapToDestinationDTO).toList();
    }

    /**
     * Update the content of a Trip.
     */
    public TripDTO patchTrip(UUID tripId, TripPatchDTO newTripDTO) {

        Trip previousTrip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        Trip trip = tripMapper.updateTripFromDto(newTripDTO, previousTrip);

        // log.info(String.format("%s, %s, %s", previousTrip.getIsInitialized(),
        // newTripDTO.isInitialized(),
        // trip.getIsInitialized()));

        return tripMapper.mapToTripDTO(tripRepository.save(trip));
    }

    /**
     * Update the content of a Trip.
     */
    public void deleteTrip(UUID tripId) {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        UserAccount userAccount = trip.getUserAccount();

        userAccount.removeTrip(trip);
        userAccountRepository.save(userAccount);
    }

    private Trip updateTodoPreset(Trip trip) {
        if (trip.getIsInitialized()) {
            return trip;
        } else if (trip.getIsTodoPresetUpdated()) {
            return trip;

        } else {
            Set<String> nationCodeset = trip.getDestinationsDirectly().stream()
                    .map(destination -> destination.getIso2DigitNationCode()).collect(Collectors.toSet());

            TodoPresetType todoPresetType = TodoPresetType.DEFAULT;
            if (nationCodeset.contains("JP")) {
                todoPresetType = TodoPresetType.JAPAN;
            } else if (nationCodeset.stream().anyMatch(code -> !code.equals("KR"))) {
                todoPresetType = TodoPresetType.FOREIGN;
            } else if (nationCodeset.contains("KR")) {
                todoPresetType = TodoPresetType.DOMESTIC;
            }

            TodoPreset preset = todoPresetRepository.findByType(todoPresetType)
                    .orElseThrow(() -> new NotFoundException(null));
            trip.setTodoPreset(preset);
            trip.setIsTodoPresetUpdated(true);
            return tripRepository.save(trip);
        }
    }

    /**
     * Create new todo.
     */
    public List<TodoPresetItemDTO> getTodoPreset(UUID tripId) {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        /* Update TodoPreset Based on Destinations */
        trip = updateTodoPreset(trip);

        List<TodoPresetItemDTO> preset = trip.getTodoPreset().getTodoPresetStockTodoContents().stream()
                .sorted(Comparator.comparingInt(TodoPresetStockTodoContent::getOrderKey)).map(
                        todoMapper::mapToTodoPresetItemDTO)
                .collect(Collectors.toList());

        Boolean doRecommendFlight = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId))
                .getDestinationsDirectly().stream()
                .anyMatch(dest -> dest.getIso2DigitNationCode() != null && dest.getIso2DigitNationCode() != "KR");
        // .getDestinationsDirectly().stream().anyMatch(dest ->
        // dest.getRecommendedOutboundFlight().size() > 0);

        /*
         * Add 2 preset items; ouutbound flight reservation & return flight reservation
         */
        if (doRecommendFlight) {

            List<FlightRouteDTO> recommendedOutboudFlight = trip.getOutboundFlights().stream()
                    .map(flightRouteMapper::mapToFlightRouteDTO).toList();
            List<FlightRouteDTO> recommendedReturnFlight = trip.getReturnFlights().stream()
                    .map(flightRouteMapper::mapToFlightRouteDTO).toList();

            log.info(Utils.asJsonString(recommendedOutboudFlight));
            log.info(Utils.asJsonString(recommendedReturnFlight));

            preset.addAll(List.of(
                    new TodoPresetItemDTO(true,
                            TodoContentDTO.builder().id(UUID.nameUUIDFromBytes("outbound-flight".getBytes()))
                                    .isStock(false).category(TodoCategory.RESERVATION).type("FLIGHT_OUTBOUND")
                                    .title("항공권 구매").icon(new Icon("🛫")).subtitle("출발 비행기")
                                    .flightRoutes(recommendedOutboudFlight).build()),

                    new TodoPresetItemDTO(true,
                            TodoContentDTO.builder().id(UUID.nameUUIDFromBytes("return-flight".getBytes()))
                                    .isStock(false).category(TodoCategory.RESERVATION).type("FLIGHT_RETURN")
                                    .title("항공권 구매").icon(new Icon("🛬")).subtitle("돌아오는 비행기")
                                    .flightRoutes(recommendedReturnFlight)
                                    .build())));
        }
        return preset;
    }

    /**
     * Add a destination to trip. If the destination doesn't exist in DB, create new
     * one and then add it.
     */
    @Transactional
    public DestinationDTO addDestination(UUID tripId, DestinationDTO destinationDTO) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        Destination destination = destinationRepository
                .findByiso2DigitNationCodeAndTitle(destinationDTO.iso2DigitNationCode(), destinationDTO.title())
                .orElseGet(
                        () -> {
                            Destination dest = tripMapper.mapToDestination(destinationDTO);
                            dest = destinationRepository.save(dest);
                            log.info(String.format(
                                    "[addDestination] publishing new NewDestinationCreatedEvent(this, %s)",
                                    dest.getId()));
                            eventPublisher.publishEvent(new NewDestinationCreatedEvent(this, dest.getId()));
                            return dest;
                        });

        destination = destinationRepository.save(destination);

        if (!tripDestinationRepository.existsById_TripIdAndId_DestinationId(tripId, destination.getId())) {

            TripDestination tripDestination = TripDestination.builder()
                    .id(new TripDestinationId(tripId, destination.getId())).trip(trip).destination(destination).build();
            trip.getDestinations().add(tripDestination);
            trip.setIsTodoPresetUpdated(false);
            tripRepository.save(trip);
        }

        return tripMapper.mapToDestinationDTO(destination);
    }

    /**
     * Create new todo.
     */
    public void deleteDestination(UUID tripId, UUID destinationId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException(tripId));
        trip.removeDestination(destinationId);
        tripRepository.save(trip);
    }

    // /**
    // * Provide the details of a Accomodation with the given trip_id.
    // */
    // public List<AccomodationDTO> getAccomodation(String tripId) {
    // List<AccomodationDTO> accomodations = tripRepository.findById(tripId)
    // .orElseThrow(() -> new
    // TripNotFoundException(tripId)).getAccomodation().stream()
    // .map(accomodation -> tripMapper.mapToAccomodationDTO(accomodation)).toList();
    // return accomodations;
    // }

    // /**
    // * Create new empty accomodation.
    // */
    // public AccomodationDTO createAccomodation(String tripId) {
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));
    // Accomodation newAccomodation = new Accomodation();
    // // newAccomodation.setTrip(trip);
    // trip.getAccomodation().add(newAccomodation);
    // return
    // tripMapper.mapToAccomodationDTO(tripRepository.save(trip).getAccomodation().getLast());
    // }

    // /**
    // * Change contents of accomodation.
    // */
    // public AccomodationDTO patchAccomodation(String accomodationId,
    // AccomodationDTO newAccomodationDTO) {
    // Accomodation accomodation =
    // tripMapper.updateAccomodationFromDto(newAccomodationDTO,
    // accomodationRepository.findById(accomodationId)
    // .orElseThrow(() -> new NotFoundException(accomodationId)));

    // return
    // tripMapper.mapToAccomodationDTO(accomodationRepository.save(accomodation));
    // }

    /**
     * Create new empty custom todo.
     */
    // public TodoDTO createCustomTodo(String tripId, String category) {
    // Todo newTodo = new Todo();
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));
    // newTodo.setOrderKey(0);
    // newTodo.setTrip(trip);
    // newTodo.setCustomTodoContent(new TodoContent(newTodo, category));
    // return tripMapper.mapToTodoDTO(todoRepository.save(newTodo));
    // }

    /**
     * Create new preset todo.
     */
    // public List<TodoDTO> createPresetTodo(String tripId, List<String> stockIds) {
    // return stockIds.stream().map((stockId) -> {
    // Todo newTodo = new Todo();
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));
    // newTodo.setOrderKey(0);
    // newTodo.setTrip(trip);
    // newTodo.setStockTodoContent(StockTodoContentRepository.findById(stockId)
    // .orElseThrow(() -> new TodoContentNotFoundException(stockId)));
    // return tripMapper.mapToTodoDTO(todoRepository.save(newTodo));
    // }).toList();
    // }
}
