package com.matchalab.trip_todo_api.event.handler;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.matchalab.trip_todo_api.enums.TodoPresetType;
import com.matchalab.trip_todo_api.event.NewDestinationAddedEvent;
import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.trip_todo_api.repository.AirlineRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.FlightRouteRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.service.ChatModelService.ChatModelService;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Setter
public class NewDestinationAddedEventHandler {

    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private final TodoPresetRepository todoPresetRepository;

    @Autowired
    private final DestinationRepository destinationRepository;

    @Autowired
    private final FlightRouteRepository flightRouteRepository;

    @Autowired
    private final FlightRouteMapper flightRouteMapper;

    @Autowired
    private final AirlineRepository airlineRepository;

    @Autowired
    private ChatModelService chatModelService;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processNewDestinationAdded(NewDestinationAddedEvent event) {

        log.info(String.format("[processNewDestinationAdded] tripId: %s", event.getTripId()));
        UUID id = event.getTripId();
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        trip.setTodoPreset(
                todoPresetRepository.findByType(TodoPresetType.DEFAULT).orElseThrow(() -> new NotFoundException(null)));
        tripRepository.save(trip);
    }
}