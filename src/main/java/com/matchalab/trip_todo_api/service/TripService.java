package com.matchalab.trip_todo_api.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.event.NewTripCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.exception.TripNotFoundException;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.AccomodationDTO;
import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.FlightTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.Todo.TodoContent;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.Todo.TodoPresetItem;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.model.mapper.TodoMapper;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.repository.AccomodationRepository;
import com.matchalab.trip_todo_api.repository.CustomTodoContentRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetRepository;
import com.matchalab.trip_todo_api.repository.TodoRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {
    @Autowired
    private final UserAccountRepository userAccountRepository;
    @Autowired
    private final TripRepository tripRepository;
    @Autowired
    private final TodoRepository todoRepository;
    @Autowired
    private final StockTodoContentRepository StockTodoContentRepository;
    @Autowired
    private final CustomTodoContentRepository customTodoContentRepository;
    @Autowired
    private final DestinationRepository destinationRepository;
    @Autowired
    private final AccomodationRepository accomodationRepository;
    @Autowired
    private final TodoPresetRepository todoPresetRepository;

    @Autowired
    private final TripMapper tripMapper;

    @Autowired
    private final TodoMapper todoMapper;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Provide the details of a Trip with the given id.
     */
    public TripDTO getTrip(String tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        return tripMapper.mapToTripDTO(trip);
    }

    /**
     * Update the content of a Trip.
     */
    public TripDTO patchTrip(String tripId, TripDTO newTripDTO) {

        Trip previousTrip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        Trip trip = tripMapper.updateTripFromDto(newTripDTO, previousTrip);

        // log.info(String.format("%s, %s, %s", previousTrip.getIsInitialized(),
        // newTripDTO.isInitialized(),
        // trip.getIsInitialized()));

        return tripMapper.mapToTripDTO(tripRepository.save(trip));
    }

    /**
     * Create new todo.
     */
    // public List<FlightRoute> getFlightRoute(String tripId) {
    // return tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId))
    // .getFlightRoute();
    // }

    /**
     * Create new todo.
     */
    @Transactional
    public TodoDTO createTodo(String tripId, TodoDTO todoDTO) {
        Todo newTodo = todoMapper.mapToTodo(todoDTO);
        log.info(Utils.asJsonString(todoDTO));
        log.info(Utils.asJsonString(newTodo));
        newTodo.setId(null);
        newTodo.setOrderKey(0);
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        trip.addTodo(newTodo);
        tripRepository.save(trip);

        return todoMapper.mapToTodoDTO(newTodo);
    }

    /**
     * Change contents or orderKey of todo.
     */
    public TodoDTO patchTodo(String todoId, TodoDTO newTodoDTO) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new NotFoundException(todoId));
        log.info(Utils.asJsonString(newTodoDTO));
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(todo)));
        if (todo.getCustomTodoContent() != null) {
            CustomTodoContent todoContent = todoMapper.updateCustomTodoContentFromDto(newTodoDTO,
                    todo.getCustomTodoContent());
            customTodoContentRepository.save(todoContent);
        }
        Todo updatedTodo = todoMapper.updateTodoFromDto(newTodoDTO, todo);
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(updatedTodo)));

        TodoDTO todoDTO = todoMapper.mapToTodoDTO(todoRepository.save(updatedTodo));
        log.info(Utils.asJsonString(todoDTO));
        return todoDTO;
    }

    /**
     * Create new todo.
     */
    public void deleteTodo(String todoId) {
        todoRepository.findById(todoId).ifPresentOrElse(entity -> todoRepository.delete(entity),
                () -> new NotFoundException(todoId));
    }

    /**
     * Create new todo.
     */
    public List<TodoPresetItem> getTodoPreset(String tripId) {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        /* If preset isn't prepared(linked), make one. */
        if (trip.getTodoPreset() == null) {
            TodoPreset preset = todoPresetRepository.findByTitle("기본").orElseThrow(() -> new NotFoundException(null));
            trip.setTodoPreset(preset);
            trip = tripRepository.save(trip);
        }

        List<TodoPresetItem> preset = trip.getTodoPreset().getTodoPresetStockTodoContent().stream().map(
                association -> new TodoPresetItem(association.getIsFlaggedToAdd(), association.getStockTodoContent()))
                .toList();

        Boolean doRecommendFlight = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId))
                .getDestination().stream().anyMatch(dest -> dest.getRecommendedOutboundFlight().size() > 0);

        /*
         * Add 2 preset items; ouutbound flight reservation & return flight reservation
         */
        if (doRecommendFlight) {

            List<FlightRoute> recommendedOutboudFlight = trip.getDestination().stream()
                    .map(dest -> dest.getRecommendedOutboundFlight()).flatMap(List::stream)
                    .collect(Collectors.toList());

            List<FlightRoute> recommendedReturnFlight = trip.getDestination().stream()
                    .map(dest -> dest.getRecommendedReturnFlight()).flatMap(List::stream)
                    .collect(Collectors.toList());

            preset.addAll(Arrays.asList(
                    new TodoPresetItem(true,
                            CustomTodoContent.builder().isStock(false).category("reservation").type("flight")
                                    .title("가는 항공편").icon(new Icon("✈️"))
                                    .flightTodoContent(new FlightTodoContent(null, recommendedOutboudFlight)).build()),

                    new TodoPresetItem(true,
                            CustomTodoContent.builder().isStock(false).category("reservation").type("flight")
                                    .title("오는 항공편").icon(new Icon("✈️"))
                                    .flightTodoContent(new FlightTodoContent(null, recommendedReturnFlight))
                                    .build())));
        }
        return preset;
    }

    /**
     * Create new empty trip.
     */
    public DestinationDTO createDestination(String tripId, DestinationDTO destinationDTO) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        destinationRepository
                .findByiso2DigitNationCodeAndTitle(destinationDTO.iso2DigitNationCode(), destinationDTO.title())
                .map(dest -> {
                    trip.getDestination().add(dest);
                    destinationRepository.save(dest);
                    return dest;
                })
                .orElseGet(
                        () -> {
                            Destination dest = tripMapper.mapToDestination(destinationDTO);
                            trip.getDestination().add(dest);
                            dest = destinationRepository.save(dest);
                            eventPublisher.publishEvent(new NewDestinationCreatedEvent(this, dest.getId()));
                            return dest;
                        });

        return tripMapper.mapToDestinationDTO(tripRepository.save(trip).getDestination().getLast());
    }

    /**
     * Create new todo.
     */
    public void deleteDestination(String destinationId) {
        destinationRepository.findById(destinationId).ifPresentOrElse(entity -> destinationRepository.delete(entity),
                () -> new NotFoundException(destinationId));
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
     * Create new todo.
     */
    public void deleteAccomodation(String accomodationId) {
        accomodationRepository.findById(accomodationId).ifPresentOrElse(entity -> accomodationRepository.delete(entity),
                () -> new NotFoundException(accomodationId));
    }
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
