package com.matchalab.trip_todo_api.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.matchalab.trip_todo_api.enums.TodoPresetType;
import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.exception.TripNotFoundException;
import com.matchalab.trip_todo_api.mapper.TodoMapper;
import com.matchalab.trip_todo_api.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoPresetItemDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.DTO.TripPatchDTO;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.Todo.FlightTodoContent;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;

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
    private final TripMapper tripMapper;

    @Autowired
    private final TodoMapper todoMapper;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    private final int maxNumberOfTrip;

    public TripService(UserAccountRepository userAccountRepository,
            TripRepository tripRepository,
            DestinationRepository destinationRepository,
            TodoPresetRepository todoPresetRepository,
            TripMapper tripMapper,
            TodoMapper todoMapper,
            ApplicationEventPublisher eventPublisher,
            @Value("${app.max-number-of-trip}") int maxNumberOfTrip) {
        this.userAccountRepository = userAccountRepository;
        this.tripRepository = tripRepository;
        this.destinationRepository = destinationRepository;
        this.todoPresetRepository = todoPresetRepository;
        this.tripMapper = tripMapper;
        this.todoMapper = todoMapper;
        this.eventPublisher = eventPublisher;
        this.maxNumberOfTrip = maxNumberOfTrip;
    }

    public TripDTO createTrip(UUID userId) {

        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId));

        if (userAccount.getTrips().size() >= maxNumberOfTrip) {
            while (userAccount.getTrips().size() >= maxNumberOfTrip) {
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
        userAccount.setActiveTripId(trip.getId());
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

    /**
     * Create new todo.
     */
    public List<TodoPresetItemDTO> getTodoPreset(UUID tripId) {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        List<TodoPresetItemDTO> preset = trip.getTodoPreset().getTodoPresetStockTodoContents().stream()
                .sorted(Comparator.comparingInt(TodoPresetStockTodoContent::getOrderKey)).map(
                        todoMapper::mapToTodoPresetItemDTO)
                .toList();

        Boolean doRecommendFlight = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId))
                .getDestinations().stream().anyMatch(dest -> dest.getRecommendedOutboundFlight().size() > 0);

        /*
         * Add 2 preset items; ouutbound flight reservation & return flight reservation
         */
        if (doRecommendFlight) {

            List<FlightRoute> recommendedOutboudFlight = trip.getDestinations().stream()
                    .map(dest -> dest.getRecommendedOutboundFlight()).flatMap(List::stream)
                    .collect(Collectors.toList());

            List<FlightRoute> recommendedReturnFlight = trip.getDestinations().stream()
                    .map(dest -> dest.getRecommendedReturnFlight()).flatMap(List::stream)
                    .collect(Collectors.toList());

            preset.addAll(Arrays.asList(
                    new TodoPresetItemDTO(true,
                            TodoContentDTO.builder().isStock(false).category("reservation").type("flight")
                                    .title("가는 항공편").icon(new Icon("✈️"))
                                    .flightTodoContent(new FlightTodoContent(null, recommendedOutboudFlight)).build()),

                    new TodoPresetItemDTO(true,
                            TodoContentDTO.builder().isStock(false).category("reservation").type("flight")
                                    .title("오는 항공편").icon(new Icon("✈️"))
                                    .flightTodoContent(new FlightTodoContent(null, recommendedReturnFlight))
                                    .build())));
        }
        return preset;
    }

    /**
     *
     * Create new empty trip.
     */
    public DestinationDTO createDestination(UUID tripId, DestinationDTO destinationDTO) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        destinationRepository
                .findByiso2DigitNationCodeAndTitle(destinationDTO.iso2DigitNationCode(), destinationDTO.title())
                .map(dest -> {
                    trip.getDestinations().add(dest);
                    destinationRepository.save(dest);
                    return dest;
                })
                .orElseGet(
                        () -> {
                            Destination dest = tripMapper.mapToDestination(destinationDTO);
                            trip.getDestinations().add(dest);
                            dest = destinationRepository.save(dest);
                            eventPublisher.publishEvent(new NewDestinationCreatedEvent(this, dest.getId()));
                            return dest;
                        });

        return tripMapper.mapToDestinationDTO(tripRepository.save(trip).getDestinations().getLast());
    }

    /**
     * Create new todo.
     */
    public void deleteDestination(UUID tripId, UUID destinationId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException(tripId));
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new NotFoundException(destinationId));
        trip.removeDestination(destination);
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
