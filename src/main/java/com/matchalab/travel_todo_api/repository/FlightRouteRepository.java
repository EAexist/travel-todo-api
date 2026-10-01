package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

// @NoRepositoryBean
public interface FlightRouteRepository extends JpaRepository<FlightRoute, UUID> {
  Optional<FlightRoute> findByDepartureIataCodeAndArrivalIataCode(
      String departureIataCode, String arrivalIataCode);
}
