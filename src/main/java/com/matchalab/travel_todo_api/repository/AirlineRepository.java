package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Flight.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, String> {}
