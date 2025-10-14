package com.matchalab.trip_todo_api.mapper;

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

import com.matchalab.trip_todo_api.model.Accomodation;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.AccomodationDTO;
import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.DTO.TripDTO;
import com.matchalab.trip_todo_api.model.DTO.TripSummaryDTO;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { TodoMapper.class })
@NoArgsConstructor
@Getter
@Setter
public abstract class TripMapper {

    @Autowired
    protected StockTodoContentRepository stockTodoContentRepository;

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
    public abstract TripDTO mapToTripDTO(Trip trip);

    @Named("mapDestinationTitle")
    public List<String> mapDestinationTitle(Trip trip) {
        return trip.getDestination().stream().map(dest -> dest.getTitle()).toList();
    }

    @Mapping(target = "destination", expression = "java(mapDestinationTitle(trip))")
    public abstract TripSummaryDTO mapToTripSummaryDTO(Trip trip);

    /*
     * mapToTrip
     */
    @Mapping(target = "destination", expression = "java(mapDestination(tripDTO))")
    // @Mapping(target = "accomodation", expression =
    // "java(mapAccomodation(tripDTO))")
    // @Mapping(target = "todolist", expression = "java(mapTodolist(tripDTO))")
    public abstract Trip mapToTrip(TripDTO tripDTO);

    // @AfterMapping
    // private void afterMapping(TodoDTO tripDTO, @MappingTarget Trip trip) {
    // List<Accomodation> accomodations =
    // trip.getAccomodation().stream().map(accomodation -> {
    // // accomodation.setTrip(trip);
    // accomodation.setTitle("HelloWorld");
    // return accomodation;
    // }).toList();
    // // log.info("accomodations", accomodations);
    // trip.setAccomodation(accomodations);

    public abstract DestinationDTO mapToDestinationDTO(Destination destination);

    @Mapping(target = "recommendedOutboundFlight", ignore = true)
    @Mapping(target = "recommendedReturnFlight", ignore = true)
    public abstract Destination mapToDestination(DestinationDTO destinationDTO);

    // @Mapping(target = "destination", expression = "java(mapDestination(tripDTO,
    // trip))")
    // @Mapping(target = "accomodation", expression = "java(mapAccomodation(tripDTO,
    // trip))")
    // @Mapping(target = "todolist", expression = "java(mapTodolist(tripDTO,
    // trip))")
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "todolist", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Trip updateTripFromDto(TripDTO tripDTO, @MappingTarget Trip trip);

    // @BeanMapping(nullValuePropertyMappingStrategy =
    // NullValuePropertyMappingStrategy.IGNORE)
    // public abstract Accomodation updateAccomodationFromDto(AccomodationDTO
    // accomodationDTO,
    // @MappingTarget Accomodation accomodation);

    /*
     * mapToTripDTO
     */

    // @Named("mapTodolist")
    // public List<TodoDTO> mapTodolist(Trip trip) {
    // return (trip.getTodolist() != null) ?
    // trip.getTodolist().stream().map(todoMapper::mapToTodoDTO).toList() : null;
    // }

    // @Named("mapTodolist")
    // public List<Todo> mapTodolist(TripDTO tripDTO) {
    // return (tripDTO.todolist() != null) ?
    // tripDTO.todolist().stream().map(todoMapper::mapToTodo).toList() : null;
    // }

    // @Named("mapTodolist")
    // public List<Todo> mapTodolist(TripDTO tripDTO, Trip trip) {
    // // log.info(String.format("[mapTodolist] trip=%s", asJsonString(trip)));
    // return (tripDTO.todolist() != null) ?
    // tripDTO.todolist().stream().map(todoMapper::mapToTodo).toList()
    // : trip.getTodolist();
    // }

    // @Named("mapAccomodation")
    // public List<AccomodationDTO> mapAccomodation(Trip trip) {
    // return (trip.getAccomodation() != null)
    // ? trip.getAccomodation().stream().map(this::mapToAccomodationDTO).toList()
    // : null;
    // }

    // @Named("mapAccomodation")
    // public List<Accomodation> mapAccomodation(TripDTO tripDTO) {
    // return (tripDTO.accomodation() != null) ?
    // tripDTO.accomodation().stream().map(this::mapToAccomodation).toList()
    // : null;
    // }

    // @Named("mapAccomodation")
    // public List<Accomodation> mapAccomodation(TripDTO tripDTO, Trip trip) {
    // // log.info(String.format("[mapAccomodation] trip=%s", asJsonString(trip)));
    // return (tripDTO.accomodation() != null) ?
    // tripDTO.accomodation().stream().map(this::mapToAccomodation).toList()
    // : trip.getAccomodation();
    // }
    // }

    // public abstract AccomodationDTO mapToAccomodationDTO(Accomodation
    // accomodation);

    // public abstract Accomodation mapToAccomodation(AccomodationDTO
    // accomodationDTO);
}