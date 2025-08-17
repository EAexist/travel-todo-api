package com.matchalab.trip_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.FlightRoute;

// @NoRepositoryBean
public interface FlightRouteRepository extends JpaRepository<FlightRoute, Long> {
}
