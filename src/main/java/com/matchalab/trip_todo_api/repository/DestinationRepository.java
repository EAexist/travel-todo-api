package com.matchalab.trip_todo_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.matchalab.trip_todo_api.model.Destination;

public interface DestinationRepository extends JpaRepository<Destination, String> {
    Optional<Destination> findByiso2DigitNationCodeAndTitle(String iso2DigitNationCode, String title);

    @Query("SELECT u FROM Destination u JOIN FETCH u.recommendedOutboundFlight JOIN FETCH u.recommendedReturnFlight WHERE u.id = :id")
    Optional<Destination> findByIdWithRecommendedFlights(String id);
}
