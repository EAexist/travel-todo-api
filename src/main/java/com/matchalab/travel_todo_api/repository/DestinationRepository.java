package com.matchalab.travel_todo_api.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.matchalab.travel_todo_api.model.Destination;

public interface DestinationRepository extends JpaRepository<Destination, UUID> {
    Optional<Destination> findByiso2DigitNationCodeAndTitle(String iso2DigitNationCode, String title);

    @Query("SELECT u FROM Destination u JOIN FETCH u.recommendedOutboundFlight JOIN FETCH u.recommendedReturnFlight WHERE u.id = :id")
    Optional<Destination> findByIdWithRecommendedFlights(String id);
}
