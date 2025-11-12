package com.matchalab.travel_todo_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.matchalab.travel_todo_api.model.Flight.Airport;
import com.matchalab.travel_todo_api.repository.AirportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/airport")
@Slf4j
public class AirportController {

    @Autowired
    private final AirportRepository airportRepository;

    /**
     * Provide the details of an Trip with the given id.
     */

    @GetMapping("/search")
    public ResponseEntity<List<Airport>> autocompleteAirport(@RequestParam String input) {

        try {
            List<Airport> airports = airportRepository.findBy(null, null);
            return ResponseEntity.ok().body(airports);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

}
