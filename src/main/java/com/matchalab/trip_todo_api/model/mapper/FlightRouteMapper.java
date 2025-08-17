package com.matchalab.trip_todo_api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.model.Airport;
import com.matchalab.trip_todo_api.model.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.Flight;
import com.matchalab.trip_todo_api.repository.AirportRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FlightRouteMapper {

    @Autowired
    protected AirportRepository airportRepository;

    private Airport getAirport(String airportIATACode) {
        return airportRepository.findById(airportIATACode).orElse(new Airport(airportIATACode));
    }

    public FlightRoute mapToFlightRoute(Flight flight) {

        return new FlightRoute(null, getAirport(flight.departureAirportIATACode()),
                getAirport(flight.arrivalAirportIATACode()), flight.airlineCompanyIATACode());

    }

}