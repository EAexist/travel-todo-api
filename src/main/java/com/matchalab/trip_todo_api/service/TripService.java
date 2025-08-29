package com.matchalab.trip_todo_api.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.exception.TripNotFoundException;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.AccomodationDTO;
import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.model.DTO.PresetDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.PresetTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.repository.AccomodationRepository;
import com.matchalab.trip_todo_api.repository.CustomTodoContentRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.PresetTodoContentRepository;
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
    private final PresetTodoContentRepository presetTodoContentRepository;
    @Autowired
    private final CustomTodoContentRepository customTodoContentRepository;
    @Autowired
    private final DestinationRepository destinationRepository;
    @Autowired
    private final AccomodationRepository accomodationRepository;
    @Autowired
    private final TripMapper tripMapper;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Provide the details of a Trip with the given id.
     */
    public TripDTO getTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        return tripMapper.mapToTripDTO(trip);
    }

    /**
     * Create new empty trip.
     */
    public TripDTO createTrip(Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId));
        Trip trip = new Trip();
        trip.setUserAccount(userAccount);
        return tripMapper.mapToTripDTO(tripRepository.save(trip));
    }

    /**
     * Update the content of a Trip.
     */
    public TripDTO patchTrip(Long tripId, TripDTO newTripDTO) {

        Trip previousTrip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        Trip trip = tripMapper.updateTripFromDto(newTripDTO, previousTrip);

        return tripMapper.mapToTripDTO(tripRepository.save(trip));
    }

    /**
     * Create new todo.
     */
    // public List<FlightRoute> getFlightRoute(Long tripId) {
    // return tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId))
    // .getFlightRoute();
    // }

    /**
     * Create new todo.
     */
    public TodoDTO createTodo(Long tripId, TodoDTO todoDTO) {
        Todo newTodo = tripMapper.mapToTodo(todoDTO);
        log.info(Utils.asJsonString(todoDTO));
        log.info(Utils.asJsonString(newTodo));
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        newTodo.setId(null);
        newTodo.setOrderKey(0);
        newTodo.setTrip(trip);
        if (newTodo.getCustomTodoContent() != null) {
            if (newTodo.getCustomTodoContent().getType() == null) {
                newTodo.getCustomTodoContent().setIcon(new Icon("⭐️", "tossface"));
                String title = "";
                // switch (newTodo.getCustomTodoContent().getCategory()) {
                // case "reservation":
                // title = "새 예약";
                // break;
                // case "foreign":
                // title = "새 할 일";
                // break;
                // case "goods":
                // title = "새 짐 챙기기";
                // break;
                // default:
                // break;
                // }
                newTodo.getCustomTodoContent().setTitle(title);
            } else {
                switch (newTodo.getCustomTodoContent().getType()) {
                    case "flight":
                        newTodo.getCustomTodoContent().setIcon(new Icon("✈️", "tossface"));
                        newTodo.getCustomTodoContent().setTitle("항공권 예약");
                        break;
                    case "flightTicket":
                        newTodo.getCustomTodoContent().setIcon(new Icon("🛫", "tossface"));
                        newTodo.getCustomTodoContent().setTitle("체크인");
                        break;
                    default:
                        newTodo.getCustomTodoContent().setIcon(new Icon("⭐️", "tossface"));
                        newTodo.getCustomTodoContent().setTitle("새 할 일");
                        break;
                }
            }
        }

        return tripMapper.mapToTodoDTO(todoRepository.save(newTodo));
    }

    /**
     * Change contents or orderKey of todo.
     */
    public TodoDTO patchTodo(Long todoId, TodoDTO newTodoDTO) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new NotFoundException(todoId));
        log.info(Utils.asJsonString(newTodoDTO));
        log.info(Utils.asJsonString(tripMapper.mapToTodoDTO(todo)));
        if (todo.getCustomTodoContent() != null) {
            CustomTodoContent customTodoContent = tripMapper.updateCustomTodoContentFromDto(newTodoDTO,
                    todo.getCustomTodoContent());
            customTodoContentRepository.save(customTodoContent);
        }
        Todo updatedTodo = tripMapper.updateTodoFromDto(newTodoDTO, todo);
        log.info(Utils.asJsonString(tripMapper.mapToTodoDTO(updatedTodo)));

        TodoDTO todoDTO = tripMapper.mapToTodoDTO(todoRepository.save(updatedTodo));
        log.info(Utils.asJsonString(todoDTO));
        return todoDTO;
    }

    /**
     * Create new todo.
     */
    public void deleteTodo(Long todoId) {
        todoRepository.findById(todoId).ifPresentOrElse(entity -> todoRepository.delete(entity),
                () -> new NotFoundException(todoId));
    }

    /**
     * Create new todo.
     */
    public List<PresetDTO> getTodoPreset(Long tripId) {
        List<PresetDTO> preset = presetTodoContentRepository.findAll().stream().map(
                presetTodoContent -> PresetDTO.builder().todo(presetTodoContent).isFlaggedToAdd(true).build())
                .toList();
        Boolean doRecommendFlight = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId))
                .getDestination().stream().anyMatch(dest -> dest.getRecommendedOutboundFlight().size() > 0);
        if (doRecommendFlight) {
            preset.addAll(Arrays.asList(
                    new PresetDTO(true, new PresetTodoContent(null, "reservation", "flight", "가는 항공편", new Icon("✈️"))),
                    new PresetDTO(true,
                            new PresetTodoContent(null, "reservation", "flight", "오는 항공편", new Icon("✈️")))));
        }
        return preset;
    }

    /**
     * Create new empty trip.
     */
    public DestinationDTO createDestination(Long tripId, DestinationDTO destinationDTO) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));

        destinationRepository
                .findByCountryISOAndTitle(destinationDTO.countryISO(), destinationDTO.title())
                .map(dest -> {
                    trip.getDestination().add(dest);
                    destinationRepository.save(dest);
                    return dest;
                })
                .orElseGet(
                        () -> {
                            Destination dest = tripMapper.mapToDestination(destinationDTO);
                            trip.getDestination().add(dest);
                            destinationRepository.save(dest);
                            eventPublisher.publishEvent(new NewDestinationCreatedEvent(this, dest.getId()));
                            return dest;
                        });

        return tripMapper.mapToDestinationDTO(tripRepository.save(trip).getDestination().getLast());
    }

    /**
     * Create new todo.
     */
    public void deleteDestination(Long destinationId) {
        destinationRepository.findById(destinationId).ifPresentOrElse(entity -> destinationRepository.delete(entity),
                () -> new NotFoundException(destinationId));
    }

    /**
     * Provide the details of a Accomodation with the given trip_id.
     */
    public List<AccomodationDTO> getAccomodation(Long tripId) {
        List<AccomodationDTO> accomodations = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId)).getAccomodation().stream()
                .map(accomodation -> tripMapper.mapToAccomodationDTO(accomodation)).toList();
        return accomodations;
    }

    /**
     * Create new empty accomodation.
     */
    public AccomodationDTO createAccomodation(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        Accomodation newAccomodation = new Accomodation();
        // newAccomodation.setTrip(trip);
        trip.getAccomodation().add(newAccomodation);
        return tripMapper.mapToAccomodationDTO(tripRepository.save(trip).getAccomodation().getLast());
    }

    /**
     * Change contents of accomodation.
     */
    public AccomodationDTO patchAccomodation(Long accomodationId, AccomodationDTO newAccomodationDTO) {
        Accomodation accomodation = tripMapper.updateAccomodationFromDto(newAccomodationDTO,
                accomodationRepository.findById(accomodationId)
                        .orElseThrow(() -> new NotFoundException(accomodationId)));

        return tripMapper.mapToAccomodationDTO(accomodationRepository.save(accomodation));
    }

    /**
     * Create new todo.
     */
    public void deleteAccomodation(Long accomodationId) {
        accomodationRepository.findById(accomodationId).ifPresentOrElse(entity -> accomodationRepository.delete(entity),
                () -> new NotFoundException(accomodationId));
    }
    /**
     * Create new empty custom todo.
     */
    // public TodoDTO createCustomTodo(Long tripId, String category) {
    // Todo newTodo = new Todo();
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));
    // newTodo.setOrderKey(0);
    // newTodo.setTrip(trip);
    // newTodo.setCustomTodoContent(new CustomTodoContent(newTodo, category));
    // return tripMapper.mapToTodoDTO(todoRepository.save(newTodo));
    // }

    /**
     * Create new preset todo.
     */
    // public List<TodoDTO> createPresetTodo(Long tripId, List<Long> presetIds) {
    // return presetIds.stream().map((presetId) -> {
    // Todo newTodo = new Todo();
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));
    // newTodo.setOrderKey(0);
    // newTodo.setTrip(trip);
    // newTodo.setPresetTodoContent(presetTodoContentRepository.findById(presetId)
    // .orElseThrow(() -> new PresetTodoContentNotFoundException(presetId)));
    // return tripMapper.mapToTodoDTO(todoRepository.save(newTodo));
    // }).toList();
    // }
}
