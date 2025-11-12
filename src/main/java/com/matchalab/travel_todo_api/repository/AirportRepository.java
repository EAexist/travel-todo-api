package com.matchalab.travel_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.travel_todo_api.model.Flight.Airport;

// @NoRepositoryBean
public interface AirportRepository extends JpaRepository<Airport, String> {
}
