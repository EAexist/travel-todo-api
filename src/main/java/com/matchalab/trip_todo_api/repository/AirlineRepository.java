package com.matchalab.trip_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Flight.Airline;

public interface AirlineRepository extends JpaRepository<Airline, String> {
}
