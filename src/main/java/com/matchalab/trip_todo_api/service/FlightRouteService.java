package com.matchalab.trip_todo_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.matchalab.trip_todo_api.event.NewFlightRouteCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.Flight.Airline;
import com.matchalab.trip_todo_api.model.Flight.FlightRoute;
import com.matchalab.trip_todo_api.model.genAI.FlightRouteWithoutAirline;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;
import com.matchalab.trip_todo_api.repository.AirlineRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.FlightRouteRepository;
import com.matchalab.trip_todo_api.service.ChatModelService.ChatModelService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Setter
@Service
public class FlightRouteService {

    @Autowired
    private final DestinationRepository destinationRepository;

    @Autowired
    private final FlightRouteRepository flightRouteRepository;

    @Autowired
    private final AirlineRepository airlineRepository;

    @Autowired
    private final FlightRouteMapper flightRouteMapper;

    @Autowired
    private ChatModelService chatModelService;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Destination> setRecommendedFlightRoutes(UUID destinationId) {

        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new NotFoundException(destinationId));

        if (destination.getIso2DigitNationCode() != null) {

            List<UUID> newlyCreatedFlightRouteIds = new ArrayList<UUID>();

            RecommendedFlightChatResult recommendedFlightChatResult = chatModelService
                    .getRecommendedFlight(destination.getTitle());

            List<FlightRoute> outboundFlightRoutes = recommendedFlightChatResult.recommendedOutboundFlight()
                    .stream()
                    .map(fr -> this.findOrCreateFlightRoute(fr, newlyCreatedFlightRouteIds))
                    .toList();

            List<FlightRoute> returnFlightRoutes = recommendedFlightChatResult.recommendedReturnFlight().stream()
                    .map(fr -> this.findOrCreateFlightRoute(fr, newlyCreatedFlightRouteIds))
                    .toList();

            destination.addRecommendedOutboundFlight(outboundFlightRoutes);
            destination.addRecommendedReturnFlight(returnFlightRoutes);

            destination = destinationRepository.save(destination);

            newlyCreatedFlightRouteIds.stream().forEach(id -> {
                eventPublisher.publishEvent(new NewFlightRouteCreatedEvent(this,
                        id));

            });
        }

        log.info(String.format("[setRecommendedFlightRoutes] destination svaed"));
        return CompletableFuture.completedFuture(destination);

    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<FlightRoute> setAirlines(UUID flightRouteId) {
        log.info(String.format("[setAirlines] flightRouteId: %s", flightRouteId));
        FlightRoute flightRoute = flightRouteRepository.findById(flightRouteId)
                .orElseThrow(() -> new NotFoundException(flightRouteId));
        List<String> airlineIcaoCodes = chatModelService.getRecommendedAirline(flightRoute);

        List<Airline> airlines = airlineIcaoCodes.stream().map(airlineRepository::findById).filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        flightRoute.getAirlines().addAll(airlines);

        log.info(String.format("flightRoute: %s", Utils.asJsonString(flightRoute)));
        return CompletableFuture.completedFuture(flightRouteRepository.save(flightRoute));
    }

    private FlightRoute findOrCreateFlightRoute(
            FlightRouteWithoutAirline frWithoutAirline,
            List<UUID> newlyCreatedIds) {
        return flightRouteRepository
                .findByDepartureIataCodeAndArrivalIataCode(
                        frWithoutAirline.departureAirportIataCode(),
                        frWithoutAirline.arrivalAirportIataCode())
                .orElseGet(() -> {
                    FlightRoute newRoute = flightRouteMapper.mapToFlightRoute(frWithoutAirline);
                    FlightRoute savedRoute = flightRouteRepository.save(newRoute);

                    newlyCreatedIds.add(savedRoute.getId());

                    return savedRoute;
                });
    }

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

}