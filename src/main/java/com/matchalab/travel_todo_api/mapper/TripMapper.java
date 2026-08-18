package com.matchalab.travel_todo_api.mapper;

import com.matchalab.travel_todo_api.DTO.*;
import com.matchalab.travel_todo_api.model.Destination;
import com.matchalab.travel_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.repository.StockTodoContentRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {TodoMapper.class})
@NoArgsConstructor
@Getter
@Setter
public abstract class TripMapper {

  @Autowired protected StockTodoContentRepository stockTodoContentRepository;

  @Autowired protected TodoMapper todoMapper;

  protected <T> T unwrapJsonNullable(JsonNullable<T> nullable, @TargetType Class<T> targetType) {
    if (nullable == null || !nullable.isPresent()) {
      return null;
    }
    return nullable.get();
  }

  /*
   * domain -> dto
   */
  @Named("mapToDestinationDTOs")
  public List<DestinationDTO> mapToDestinationDTOs(Trip trip) {
    return (trip.getDestinationsDirectly() != null)
        ? trip.getDestinationsDirectly().stream().map(this::mapToDestinationDTO).toList()
        : null;
  }

  @Named("mapToStockTodoContents")
  public List<TodoContentDTO> mapToStockTodoContents(Trip trip) {
    return trip.getTodoPreset() != null
        ? trip.getTodoPreset().getTodoPresetStockTodoContents().stream()
            .map(TodoPresetStockTodoContent::getStockTodoContent)
            .map(todoMapper::mapToTodoContentDTO)
            .toList()
        : new ArrayList<TodoContentDTO>();
  }

  @Mapping(target = "destinations", expression = "java(mapToDestinationDTOs(trip))")
  @Mapping(target = "stockTodoContents", expression = "java(mapToStockTodoContents(trip))")
  public abstract TripDTO mapToTripDTO(Trip trip);

  @Named("mapToDestinationTitles")
  public List<String> mapToDestinationTitles(Trip trip) {
    return trip.getDestinationsDirectly().stream().map(dest -> dest.getTitle()).toList();
  }

  @Mapping(target = "destinationTitles", expression = "java(mapToDestinationTitles(trip))")
  public abstract TripSummaryDTO mapToTripSummaryDTO(Trip trip);

  /*
   * dto -> domain
   */
  @Mapping(target = "reservations", ignore = true)
  @Mapping(target = "destinations", ignore = true)
  @Mapping(target = "todolist", ignore = true)
  public abstract Trip mapToTrip(TripDTO tripDTO);

  @Mapping(target = "reservations", ignore = true)
  @Mapping(target = "destinations", ignore = true)
  @Mapping(target = "todolist", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract Trip updateTripFromDto(TripPatchDTO tripDTO, @MappingTarget Trip trip);

  @Named("mapToDestinations")
  public List<Destination> mapToDestinations(TripDTO tripDTO) {
    return (tripDTO.destinations() != null)
        ? tripDTO.destinations().stream().map(this::mapToDestination).toList()
        : null;
  }

  /*
   * Destinations
   */
  public abstract DestinationDTO mapToDestinationDTO(Destination destination);

  @Mapping(target = "recommendedOutboundFlight", ignore = true)
  @Mapping(target = "recommendedReturnFlight", ignore = true)
  public abstract Destination mapToDestination(DestinationDTO destinationDTO);
}
