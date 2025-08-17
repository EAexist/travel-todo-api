package com.matchalab.trip_todo_api.service.EventHandler;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.matchalab.trip_todo_api.event.NewDestinationCreatedEvent;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.model.genAI.RecommendedFlightChatResult;
import com.matchalab.trip_todo_api.model.mapper.FlightRouteMapper;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.service.GenAIService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewEntityCreatedEventHandlerService {

    @Autowired
    private final DestinationRepository destinationRepository;

    @Autowired
    private final FlightRouteMapper flightRouteMapper;

    @Autowired
    private final GenAIService genAIService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<Destination> processNewDestinationAsync(NewDestinationCreatedEvent event) {
        Long id = event.getDestinationId();
        Destination destination = destinationRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        RecommendedFlightChatResult recommendedFlightChatResult = genAIService
                .getRecommendedFlight(destination.getTitle());
        destination.setRecommendedOutboundFlight(recommendedFlightChatResult.recommendedOutboundFlight().stream()
                .map(flightRouteMapper::mapToFlightRoute).toList());
        destination.setRecommendedReturnFlight(recommendedFlightChatResult.recommendedReturnFlight().stream()
                .map(flightRouteMapper::mapToFlightRoute).toList());

        log.info(String.format("destination: %s", Utils.asJsonString(destination)));
        return CompletableFuture.completedFuture(destinationRepository.save(destination));
    }
}