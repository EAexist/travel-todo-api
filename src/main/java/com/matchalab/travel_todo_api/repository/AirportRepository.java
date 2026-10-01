package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Flight.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

// @NoRepositoryBean
public interface AirportRepository extends JpaRepository<Airport, String> {}
