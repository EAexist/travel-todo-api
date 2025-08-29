package com.matchalab.trip_todo_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Flight.FlightRoute;

// @NoRepositoryBean
public interface FlightRouteRepository extends JpaRepository<FlightRoute, Long> {
    Optional<FlightRoute> findByDepartureIATACodeAndArrivalIATACode(String departureIATACode, String arrivalIATACode);
}
