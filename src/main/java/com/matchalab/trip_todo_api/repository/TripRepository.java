package com.matchalab.trip_todo_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Trip;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    Optional<Trip> findTop1ByIsSampleTrue();
}
