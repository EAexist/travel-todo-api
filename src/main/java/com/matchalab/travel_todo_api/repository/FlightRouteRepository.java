package com.matchalab.travel_todo_api.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.travel_todo_api.model.Flight.FlightRoute;

// @NoRepositoryBean
public interface FlightRouteRepository extends JpaRepository<FlightRoute, UUID> {
    Optional<FlightRoute> findByDepartureIataCodeAndArrivalIataCode(String departureIataCode, String arrivalIataCode);
}
