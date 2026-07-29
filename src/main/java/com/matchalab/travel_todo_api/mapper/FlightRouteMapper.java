package com.matchalab.travel_todo_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.travel_todo_api.DTO.FlightRouteDTO;
import com.matchalab.travel_todo_api.model.Flight.Airport;
import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import com.matchalab.travel_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.travel_todo_api.repository.AirportRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { AirportMapper.class })
public abstract class FlightRouteMapper {

    public abstract FlightRouteDTO mapToFlightRouteDTO(FlightRoute flightRoute);

}