package com.matchalab.trip_todo_api.model.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.exception.PresetTodoContentNotFoundException;
import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.PresetTodoContent;
import com.matchalab.trip_todo_api.model.Todo;
import com.matchalab.trip_todo_api.model.TodoContent;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.AccomodationDTO;
import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.repository.PresetTodoContentRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TripMapper {

    @Autowired
    protected PresetTodoContentRepository presetTodoContentRepository;

    /*
     * mapToTodoDTO
     */

    public TodoDTO mapToTodoDTO(Todo todo) {
        TodoContent todoContent = (todo.getPresetTodoContent() != null) ? todo.getPresetTodoContent()
                : todo.getCustomTodoContent();
        return new TodoDTO(todo.getId(), todo.getOrderKey(), todo.getNote(), todo.getCompleteDateISOString(),
                (todo.getPresetTodoContent() != null) ? todo.getPresetTodoContent().getId() : null,
                todoContent.getCategory(), todoContent.getType(),
                todoContent.getTitle(), todoContent.getIcon(), todo.getDeparture(),
                todo.getArrival());

    }

    /*
     * mapToTodo
     */
    @Named("mapCustomTodoContent")
    public CustomTodoContent mapCustomTodoContent(TodoDTO todoDTO) {
        return todoDTO.presetId() != null ? null
                : new CustomTodoContent(null, null, todoDTO.category(), todoDTO.type(), todoDTO.title(),
                        todoDTO.icon());
    }

    // @Named("mapCustomTodoContent")
    // public CustomTodoContent mapCustomTodoContent(TodoDTO todoDTO, Todo todo) {
    // return updateCustomTodoContentFromDto(todoDTO, todo.getCustomTodoContent());
    // }

    @Named("mapCustomTodoContent")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract CustomTodoContent updateCustomTodoContentFromDto(TodoDTO todoDTO,
            @MappingTarget CustomTodoContent customTodoContent);

    @Named("mapPresetTodoContent")
    public PresetTodoContent mapPresetTodoContent(TodoDTO todoDTO) {
        return todoDTO.presetId() != null
                ? presetTodoContentRepository.findById(todoDTO.presetId())
                        .orElseThrow(() -> new PresetTodoContentNotFoundException(todoDTO.presetId()))
                // new PresetTodoContent(null, null, todoDTO.category(), todoDTO.type(),
                // todoDTO.title(),
                // todoDTO.iconId())
                : null;
    }

    @Mapping(target = "customTodoContent", expression = "java(mapCustomTodoContent(todoDTO))")
    @Mapping(target = "presetTodoContent", expression = "java(mapPresetTodoContent(todoDTO))")
    public abstract Todo mapToTodo(TodoDTO todoDTO);

    // @AfterMapping
    // private void afterMapping(TodoDTO todoDTO, @MappingTarget Todo todo) {
    // todo.getCustomTodoContent().setTodo(todo);
    // }

    /*
     * mapToTripDTO
     */

    @Named("mapTodolist")
    public List<TodoDTO> mapTodolist(Trip trip) {
        return (trip.getTodolist() != null) ? trip.getTodolist().stream().map(this::mapToTodoDTO).toList() : null;
    }

    @Named("mapTodolist")
    public List<Todo> mapTodolist(TripDTO tripDTO) {
        return (tripDTO.todolist() != null) ? tripDTO.todolist().stream().map(this::mapToTodo).toList() : null;
    }

    @Named("mapTodolist")
    public List<Todo> mapTodolist(TripDTO tripDTO, Trip trip) {
        // log.info(String.format("[mapTodolist] trip=%s", asJsonString(trip)));
        return (tripDTO.todolist() != null) ? tripDTO.todolist().stream().map(this::mapToTodo).toList()
                : trip.getTodolist();
    }

    @Named("mapAccomodation")
    public List<AccomodationDTO> mapAccomodation(Trip trip) {
        return (trip.getAccomodation() != null)
                ? trip.getAccomodation().stream().map(this::mapToAccomodationDTO).toList()
                : null;
    }

    @Named("mapAccomodation")
    public List<Accomodation> mapAccomodation(TripDTO tripDTO) {
        return (tripDTO.accomodation() != null) ? tripDTO.accomodation().stream().map(this::mapToAccomodation).toList()
                : null;
    }

    @Named("mapAccomodation")
    public List<Accomodation> mapAccomodation(TripDTO tripDTO, Trip trip) {
        // log.info(String.format("[mapAccomodation] trip=%s", asJsonString(trip)));
        return (tripDTO.accomodation() != null) ? tripDTO.accomodation().stream().map(this::mapToAccomodation).toList()
                : trip.getAccomodation();
    }

    @Named("mapDestination")
    public List<DestinationDTO> mapDestination(Trip trip) {
        return (trip.getDestination() != null) ? trip.getDestination().stream().map(this::mapToDestinationDTO).toList()
                : null;
    }

    @Named("mapDestination")
    public List<Destination> mapDestination(TripDTO tripDTO) {
        return (tripDTO.destination() != null) ? tripDTO.destination().stream().map(this::mapToDestination).toList()
                : null;
    }

    @Named("mapDestination")
    public List<Destination> mapDestination(TripDTO tripDTO, Trip trip) {
        // log.info(String.format("[mapDestination] trip=%s", asJsonString(trip)));
        return (tripDTO.destination() != null) ? tripDTO.destination().stream().map(this::mapToDestination).toList()
                : trip.getDestination();
    }

    @Mapping(target = "destination", expression = "java(mapDestination(trip))")
    @Mapping(target = "accomodation", expression = "java(mapAccomodation(trip))")
    @Mapping(target = "todolist", expression = "java(mapTodolist(trip))")
    public abstract TripDTO mapToTripDTO(Trip trip);

    /*
     * mapToTrip
     */
    @Mapping(target = "destination", expression = "java(mapDestination(tripDTO))")
    @Mapping(target = "accomodation", expression = "java(mapAccomodation(tripDTO))")
    @Mapping(target = "todolist", expression = "java(mapTodolist(tripDTO))")
    public abstract Trip mapToTrip(TripDTO tripDTO);

    @AfterMapping
    private void afterMapping(TodoDTO tripDTO, @MappingTarget Trip trip) {
        List<Accomodation> accomodations = trip.getAccomodation().stream().map(accomodation -> {
            accomodation.setTrip(trip);
            accomodation.setTitle("HelloWorld");
            return accomodation;
        }).toList();
        // log.info("accomodations", accomodations);
        trip.setAccomodation(accomodations);
    }

    public abstract AccomodationDTO mapToAccomodationDTO(Accomodation accomodation);

    public abstract Accomodation mapToAccomodation(AccomodationDTO accomodationDTO);

    public abstract DestinationDTO mapToDestinationDTO(Destination destination);

    @Mapping(target = "recommendedOutboundFlight", ignore = true)
    @Mapping(target = "recommendedReturnFlight", ignore = true)
    public abstract Destination mapToDestination(DestinationDTO destinationDTO);

    // public PresetDTO mapToPresetDTO(PresetTodoContent presetTodoContent) {

    // }

    // @Mapping(target = "destination", expression = "java(mapDestination(tripDTO,
    // trip))")
    // @Mapping(target = "accomodation", expression = "java(mapAccomodation(tripDTO,
    // trip))")
    // @Mapping(target = "todolist", expression = "java(mapTodolist(tripDTO,
    // trip))")
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "accomodation", ignore = true)
    @Mapping(target = "todolist", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Trip updateTripFromDto(TripDTO tripDTO, @MappingTarget Trip trip);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract TodoDTO updateTodoDTOFromDto(TodoDTO todoDTOSource, @MappingTarget TodoDTO todoDTOTarget);

    // @Mapping(target = "customTodoContent", expression =
    // "java(updateCustomTodoContentFromDto(todoDTO, todo.getCustomTodoContent()))")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Todo updateTodoFromDto(TodoDTO todoDTO, @MappingTarget Todo todo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Accomodation updateAccomodationFromDto(AccomodationDTO accomodationDTO,
            @MappingTarget Accomodation accomodation);

    // protected static String asJsonString(final Object obj) {
    // try {
    // final ObjectMapper mapper = new ObjectMapper();
    // final String jsonContent = mapper.writeValueAsString(obj);
    // return jsonContent;
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }
}