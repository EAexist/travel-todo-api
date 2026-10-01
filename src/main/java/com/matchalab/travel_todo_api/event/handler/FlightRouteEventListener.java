package com.matchalab.travel_todo_api.event.handler;

import com.matchalab.travel_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.travel_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.travel_todo_api.model.Destination;
import com.matchalab.travel_todo_api.model.Flight.FlightRoute;
import com.matchalab.travel_todo_api.service.FlightRouteService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Setter
public class FlightRouteEventListener {

  @Autowired private final FlightRouteService flightRouteService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void setRecommendedFlightRoutes(NewDestinationCreatedEvent event) {

    CompletableFuture<Destination> destination =
        flightRouteService.setRecommendedFlightRoutes(event.getDestinationId());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void setAirlines(NewFlightRouteCreatedEvent event) {

    CompletableFuture<FlightRoute> flightRoute =
        flightRouteService.setAirlines(event.getFlightRouteId());
  }
}
