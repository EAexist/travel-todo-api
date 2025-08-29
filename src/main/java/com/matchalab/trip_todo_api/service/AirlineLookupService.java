package com.matchalab.trip_todo_api.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.matchalab.trip_todo_api.model.Flight.Airline;
import com.matchalab.trip_todo_api.repository.AirlineRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AirlineLookupService {
    private final AirlineRepository repository;
    private final Map<String, String> airlineLookupMap = new ConcurrentHashMap<>();

    public AirlineLookupService(AirlineRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadAirlineLookupMap() {
        airlineLookupMap.putAll(
                repository.findAll().stream()
                        .collect(Collectors.toMap(Airline::getIATACode, Airline::getName)));
    }

    public Optional<String> get(String IATACode) {
        return Optional.ofNullable(airlineLookupMap.getOrDefault(IATACode, null));
    }
}