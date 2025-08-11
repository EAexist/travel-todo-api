package com.matchalab.trip_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Airport;

// @NoRepositoryBean
public interface AirportRepository extends JpaRepository<Airport, Long> {
}
