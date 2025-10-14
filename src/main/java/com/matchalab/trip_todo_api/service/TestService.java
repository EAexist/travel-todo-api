package com.matchalab.trip_todo_api.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.matchalab.trip_todo_api.model.Destination;
import com.matchalab.trip_todo_api.repository.DestinationRepository;

@Service
public class TestService {
    @Autowired
    private DestinationRepository destinationRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED) // New transaction
    public Destination findDestinationById(UUID destinationId) {

        Destination destination = destinationRepository.findById(destinationId).orElseThrow();
        destination.getRecommendedOutboundFlight().size();
        destination.getRecommendedReturnFlight().size();

        return destination;
    }

}
