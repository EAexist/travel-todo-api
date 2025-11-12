package com.matchalab.travel_todo_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.matchalab.travel_todo_api.DTO.DestinationDTO;
import com.matchalab.travel_todo_api.DTO.TodoPresetItemDTO;
import com.matchalab.travel_todo_api.DTO.TripDTO;
import com.matchalab.travel_todo_api.DTO.TripPatchDTO;
import com.matchalab.travel_todo_api.DTO.UserAccountDTO;
import com.matchalab.travel_todo_api.service.TripService;
import com.matchalab.travel_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping()
@Slf4j
public class TripController {

    @Autowired
    private final TripService tripService;

    /**
     * Create New Empty Trip that is owned by UserAccount@userId.
     */
    @PostMapping("user/{userId}/trip")
    public ResponseEntity<TripDTO> createTrip(@PathVariable UUID userId) {
        try {

            TripDTO tripDTO = tripService.createTrip(userId);

            return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .replacePath("/trip/{tripId}")
                    .buildAndExpand(tripDTO.id())
                    .toUri())
                    .body(tripDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    @GetMapping("trip/{tripId}")
    public ResponseEntity<TripDTO> trip(@PathVariable UUID tripId) {
        try {
            return ResponseEntity.ok().body(tripService.getTrip(tripId));
        } catch (HttpClientErrorException e) {
            throw e;
            // return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update the metadata(title, destination, schedule) of a Trip.
     */
    @PatchMapping("trip/{tripId}")
    public ResponseEntity<TripDTO> patchTrip(@PathVariable UUID tripId, @RequestBody TripPatchDTO newTripDTO) {
        try {
            return ResponseEntity.ok().body(tripService.patchTrip(tripId, newTripDTO));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the todo preset of a Trip.
     */
    @GetMapping("trip/{tripId}/todoPreset")
    public ResponseEntity<List<TodoPresetItemDTO>> getTodoPreset(@PathVariable UUID tripId) {
        try {
            List<TodoPresetItemDTO> todoPresetItems = tripService.getTodoPreset(tripId);
            return ResponseEntity.ok().body(todoPresetItems);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Get a trip's destinations.
     */
    @GetMapping("trip/{tripId}/destination")
    public ResponseEntity<List<DestinationDTO>> getDestinations(@PathVariable UUID tripId) {
        try {
            List<DestinationDTO> destinationDTO = tripService.getDestinations(tripId);
            return ResponseEntity.ok().body(destinationDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Add a destination to the Trip's destination list.
     * If the destination doesn't exist in databse, create new one.
     */
    @PostMapping("trip/{tripId}/destination")
    public ResponseEntity<DestinationDTO> addDestination(@PathVariable UUID tripId,
            @RequestBody DestinationDTO requestedDestinationDTO) {
        try {
            DestinationDTO destinationDTO = tripService.addDestination(tripId, requestedDestinationDTO);
            return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .replacePath("/destination/{destinationId}")
                    .buildAndExpand(destinationDTO.id())
                    .toUri()).body(destinationDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Delete a destination from the Trip's destination list.
     * This does not remove the destination from the database.
     */

    @DeleteMapping("trip/{tripId}/destination/{destinationId}")
    public ResponseEntity<Void> deleteDestination(@PathVariable UUID tripId, @PathVariable UUID destinationId) {
        try {
            tripService.deleteDestination(tripId, destinationId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    @DeleteMapping("trip/{tripId}")
    public ResponseEntity<UserAccountDTO> deleteTrip(@PathVariable UUID tripId) {
        try {
            tripService.deleteTrip(tripId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (HttpClientErrorException e) {
            throw e;
            // return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    // @GetMapping("/{tripId}/recommendedFlight")
    // public ResponseEntity<List<FlightRoute>> recommendedFlight(@PathVariable
    // String
    // tripId) {
    // try {
    // List<FlightRoute> recommendedFlight = tripService.getFlightRoute(tripId);
    // log.info(recommendedFlight.toString());
    // return ResponseEntity.ok().body(recommendedFlight);
    // } catch (HttpClientErrorException e) {
    // throw e;
    // }
    // }
}
