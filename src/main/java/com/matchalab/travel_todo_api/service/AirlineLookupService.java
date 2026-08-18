package com.matchalab.travel_todo_api.service;

import com.matchalab.travel_todo_api.repository.AirlineRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AirlineLookupService {
  private final AirlineRepository repository;

  // private final Map<String, String> airlineLookupMap = new
  // ConcurrentHashMap<>();

  public AirlineLookupService(AirlineRepository repository) {
    this.repository = repository;
  }

  // @PostConstruct
  // public void loadAirlineLookupMap() {
  // airlineLookupMap.putAll(
  // repository.findAll().stream()
  // .collect(Collectors.toMap(Airline::getIataCode, Airline::getTitle)));
  // }

  public Optional<String> get(String icaoCode) {
    return repository.findById(icaoCode).map(airline -> airline.getTitle());
  }
}
