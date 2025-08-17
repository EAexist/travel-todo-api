package com.matchalab.trip_todo_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Destination;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Optional<Destination> findByCountryISOAndTitle(String countryISO, String title);
}
