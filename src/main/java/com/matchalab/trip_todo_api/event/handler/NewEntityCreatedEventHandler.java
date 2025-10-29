package com.matchalab.trip_todo_api.event.handler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.matchalab.trip_todo_api.enums.TodoPresetType;
import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.event.NewTripCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.Flight.Airline;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;
import com.matchalab.trip_todo_api.repository.AirlineRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.FlightRouteRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.service.ChatModelService.ChatModelService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Setter
public class NewEntityCreatedEventHandler {

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

    private FlightRoute processRecommendedFlightChatResult(FlightRouteWithoutAirline frWithoutAirline) {

        return flightRouteRepository
                .findByDepartureIataCodeAndArrivalIataCode(frWithoutAirline.departureAirportIataCode(),
                        frWithoutAirline.arrivalAirportIataCode())
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
        UUID id = event.getDestinationId();
        Destination destination = destinationRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (destination.getIso2DigitNationCode() != null) {
            RecommendedFlightChatResult recommendedFlightChatResult = chatModelService
                    .getRecommendedFlight(destination.getTitle());
            destination.setRecommendedOutboundFlight(recommendedFlightChatResult.recommendedOutboundFlight().stream()
                    .map(this::processRecommendedFlightChatResult).toList());

            destination.setRecommendedReturnFlight(recommendedFlightChatResult.recommendedReturnFlight().stream()
                    .map(this::processRecommendedFlightChatResult).toList());
        }

        log.info(String.format("destination: %s", Utils.asJsonString(destination)));
        return CompletableFuture.completedFuture(destinationRepository.save(destination));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<FlightRoute> processNewFlightRouteAsync(NewFlightRouteCreatedEvent event) {
        log.info(String.format("[processNewFlightRouteAsync] flightRouteId: %s", event.getFlightRouteId()));
        UUID id = event.getFlightRouteId();
        FlightRoute flightRoute = flightRouteRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        List<String> airlineIcaoCodes = chatModelService.getRecommendedAirline(flightRoute);

        List<Airline> airlines = airlineIcaoCodes.stream().map(airlineRepository::findById).filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        flightRoute.getAirlines().addAll(airlines);

        log.info(String.format("flightRoute: %s", Utils.asJsonString(flightRoute)));
        return CompletableFuture.completedFuture(flightRouteRepository.save(flightRoute));
    }
}