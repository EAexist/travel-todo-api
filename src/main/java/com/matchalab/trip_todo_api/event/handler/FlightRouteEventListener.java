package com.matchalab.trip_todo_api.event.handler;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.service.FlightRouteService;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Setter
public class FlightRouteEventListener {

    @Autowired
    private final FlightRouteService flightRouteService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void setRecommendedFlightRoutes(NewDestinationCreatedEvent event) {
        log.info(String.format("[setRecommendedFlightRoutes] destinationId: %s", event.getDestinationId()));

        CompletableFuture<Destination> destination = flightRouteService
                .setRecommendedFlightRoutes(event.getDestinationId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void setAirlines(NewFlightRouteCreatedEvent event) {
        log.info(String.format("[setAirlines] flightRouteId: %s", event.getFlightRouteId()));

        CompletableFuture<FlightRoute> flightRoute = flightRouteService.setAirlines(event.getFlightRouteId());
    }
}