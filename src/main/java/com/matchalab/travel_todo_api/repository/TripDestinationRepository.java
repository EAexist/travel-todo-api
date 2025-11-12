package com.matchalab.travel_todo_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.travel_todo_api.model.TripDestination;
import com.matchalab.travel_todo_api.model.TripDestinationId;

public interface TripDestinationRepository extends JpaRepository<TripDestination, TripDestinationId> {

    boolean existsById_TripIdAndId_DestinationId(UUID tripId, UUID destinationId);
}
