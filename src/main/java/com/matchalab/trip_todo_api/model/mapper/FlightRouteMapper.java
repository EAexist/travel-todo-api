package com.matchalab.trip_todo_api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.trip_todo_api.repository.AirportRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FlightRouteMapper {

    @Autowired
    protected AirportRepository airportRepository;

    private Airport getAirport(String airportIataCode) {
        return airportRepository.findById(airportIataCode).orElse(new Airport(airportIataCode));
    }

    public FlightRoute mapToFlightRoute(FlightRouteWithoutAirline frWithoutAirline) {

        return new FlightRoute(getAirport(frWithoutAirline.departureAirportIataCode()),
                getAirport(frWithoutAirline.arrivalAirportIataCode()));

    }

}