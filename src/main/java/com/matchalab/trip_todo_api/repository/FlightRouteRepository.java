package com.matchalab.trip_todo_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Flight.FlightRoute;

// @NoRepositoryBean
public interface FlightRouteRepository extends JpaRepository<FlightRoute, String> {
    Optional<FlightRoute> findByDepartureIataCodeAndArrivalIataCode(String departureIataCode, String arrivalIataCode);
}
