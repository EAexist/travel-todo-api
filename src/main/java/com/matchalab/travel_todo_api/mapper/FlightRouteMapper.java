package com.matchalab.travel_todo_api.mapper;

import com.matchalab.travel_todo_api.DTO.FlightRouteDTO;
import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {AirportMapper.class})
public abstract class FlightRouteMapper {

  public abstract FlightRouteDTO mapToFlightRouteDTO(FlightRoute flightRoute);
}
