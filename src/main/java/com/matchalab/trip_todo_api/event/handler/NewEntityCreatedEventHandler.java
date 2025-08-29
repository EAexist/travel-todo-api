package com.matchalab.trip_todo_api.event.handler;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Flight.Airline;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;
import com.matchalab.trip_todo_api.model.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.repository.AirlineRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.FlightRouteRepository;
import com.matchalab.trip_todo_api.service.AirlineLookupService;
import com.matchalab.trip_todo_api.service.GenAIService;
import com.matchalab.trip_todo_api.utils.Utils;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Setter
public class NewEntityCreatedEventHandler {

    @Autowired
    private final DestinationRepository destinationRepository;

    @Autowired
    private final FlightRouteRepository flightRouteRepository;

    @Autowired
    private final FlightRouteMapper flightRouteMapper;

    @Autowired
    private final AirlineRepository airlineRepository;

    @Autowired
    private GenAIService genAIService;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    private FlightRoute processRecommendedFlightChatResult(FlightRouteWithoutAirline frWithoutAirline) {

        return flightRouteRepository
                .findByDepartureIATACodeAndArrivalIATACode(frWithoutAirline.departureAirportIATACode(),
                        frWithoutAirline.arrivalAirportIATACode())
                .orElseGet(() -> {
                    FlightRoute flightRoute = flightRouteRepository
                            .save(flightRouteMapper.mapToFlightRoute(frWithoutAirline));
                    eventPublisher.publishEvent(new NewFlightRouteCreatedEvent(this,
                            flightRoute.getId()));
                    return flightRoute;
                });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Destination> processNewDestinationAsync(NewDestinationCreatedEvent event) {
        log.info(String.format("[processNewDestinationAsync] destinationId: %s", event.getDestinationId()));
        Long id = event.getDestinationId();
        Destination destination = destinationRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        RecommendedFlightChatResult recommendedFlightChatResult = genAIService
                .getRecommendedFlight(destination.getTitle());
        destination.setRecommendedOutboundFlight(recommendedFlightChatResult.recommendedOutboundFlight().stream()
                .map(this::processRecommendedFlightChatResult).toList());

        destination.setRecommendedReturnFlight(recommendedFlightChatResult.recommendedReturnFlight().stream()
                .map(this::processRecommendedFlightChatResult).toList());

        log.info(String.format("destination: %s", Utils.asJsonString(destination)));
        return CompletableFuture.completedFuture(destinationRepository.save(destination));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<FlightRoute> processNewFlightRouteAsync(NewFlightRouteCreatedEvent event) {
        log.info(String.format("[processNewFlightRouteAsync] flightRouteId: %s", event.getFlightRouteId()));
        Long id = event.getFlightRouteId();
        FlightRoute flightRoute = flightRouteRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        List<String> airlineIATAcodes = genAIService.getRecommendedAirline(flightRoute);

        List<Airline> airlines = airlineIATAcodes.stream().map(airlineRepository::findById).filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        flightRoute.getAirlines().addAll(airlines);

        log.info(String.format("flightRoute: %s", Utils.asJsonString(flightRoute)));
        return CompletableFuture.completedFuture(flightRouteRepository.save(flightRoute));
    }
}