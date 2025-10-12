// package com.matchalab.trip_todo_api.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.client.HttpClientErrorException;

// import com.matchalab.trip_todo_api.model.DTO.AccomodationDTO;
// import com.matchalab.trip_todo_api.model.DTO.DestinationDTO;
// import com.matchalab.trip_todo_api.model.DTO.TripDTO;
// import com.matchalab.trip_todo_api.model.Todo.TodoPresetItem;
// import com.matchalab.trip_todo_api.service.TripService;
// import com.matchalab.trip_todo_api.utils.Utils;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("user/{userId}/trip")
// @Slf4j
// public class TripController {

// @Autowired
// private final TripService tripService;

// /**
// * Provide the details of a Trip with the given id.
// */
// @GetMapping("/{tripId}")
// public ResponseEntity<TripDTO> trip(@PathVariable String tripId) {
// try {
// return ResponseEntity.ok().body(tripService.getTrip(tripId));
// } catch (HttpClientErrorException e) {
// throw e;
// // return ResponseEntity.badRequest().body(e.getMessage());
// }
// }

// /**
// * Update the metadata(title, destination, schedule) of a Trip.
// */
// @PatchMapping("/{tripId}")
// public ResponseEntity<TripDTO> patchTrip(@PathVariable String tripId,
// @RequestBody TripDTO newTripDTO) {
// try {
// return ResponseEntity.ok().body(tripService.patchTrip(tripId, newTripDTO));
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// /**
// * Provide the todo preset of a Trip.
// */
// @GetMapping("/{tripId}/todoPreset")
// public ResponseEntity<List<TodoPresetItem>> getTodoPreset(@PathVariable
// String tripId) {
// try {
// List<TodoPresetItem> StockTodoContentDTOs =
// tripService.getTodoPreset(tripId);
// return ResponseEntity.ok().body(StockTodoContentDTOs);
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// /**
// * Add a destination to the Trip's destination list.
// * If the destination doesn't exist in databse, create new one.
// */
// @PostMapping("/{tripId}/destination")
// public ResponseEntity<DestinationDTO> createDestination(@PathVariable String
// tripId,
// @RequestBody DestinationDTO requestedDestinationDTO) {
// try {
// DestinationDTO destinationDTO = tripService.createDestination(tripId,
// requestedDestinationDTO);
// return
// ResponseEntity.created(Utils.getLocation(destinationDTO.id())).body(destinationDTO);
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// /**
// * Delete a destination from the Trip's destination list.
// * This does not remove the destination from the database.
// */

// @DeleteMapping("/{tripId}/destination/{destinationId}")
// public ResponseEntity<Void> deleteDestination(@PathVariable String
// destinationId) {
// try {
// tripService.deleteDestination(destinationId);
// return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// /**
// * Provide the details of an Trip with the given id.
// */
// // @GetMapping("/{tripId}/recommendedFlight")
// // public ResponseEntity<List<FlightRoute>> recommendedFlight(@PathVariable
// // String
// // tripId) {
// // try {
// // List<FlightRoute> recommendedFlight = tripService.getFlightRoute(tripId);
// // log.info(recommendedFlight.toString());
// // return ResponseEntity.ok().body(recommendedFlight);
// // } catch (HttpClientErrorException e) {
// // throw e;
// // }
// // }

// /**
// * Provide the details of an Trip with the given id.
// */
// @PostMapping("/{tripId}/accomodation")
// public ResponseEntity<AccomodationDTO> createAccomodation(@PathVariable
// String tripId) {
// try {
// AccomodationDTO accomodationDTO = tripService.createAccomodation(tripId);
// return
// ResponseEntity.created(Utils.getLocation(accomodationDTO.id())).body(accomodationDTO);
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// /**
// * Provide the details of an Trip with the given id.
// */
// @PatchMapping("/{tripId}/accomodation/{accomodationId}")
// public ResponseEntity<AccomodationDTO> patchAccomodation(@PathVariable String
// accomodationId,
// @RequestBody AccomodationDTO newAccomodationDTO) {
// try {
// AccomodationDTO accomodationDTO =
// tripService.patchAccomodation(accomodationId, newAccomodationDTO);
// return ResponseEntity.ok().body(accomodationDTO);
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// @DeleteMapping("/{tripId}/accomodation/{accomodationId}")
// public ResponseEntity<Void> deleteAccomodation(@PathVariable String
// accomodationId) {
// try {
// tripService.deleteAccomodation(accomodationId);
// return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// /**
// * Provide the details of an Trip with the given id.
// */
// // @PostMapping("/{tripId}/customTodo")
// // public ResponseEntity<TodoDTO> createCustomTodo(@PathVariable String
// tripId,
// // @RequestBody String category) {
// // try {
// // TodoDTO todoDTO = tripService.createCustomTodo(tripId, category);
// // return
// ResponseEntity.created(Utils.getLocation(todoDTO.id())).body(todoDTO);
// // } catch (HttpClientErrorException e) {
// // throw e;
// // }
// // }

// /**
// * Provide the details of an Trip with the given id.
// */
// // @PostMapping("/{tripId}/stockTodo")
// // public ResponseEntity<List<TodoDTO>> createPresetTodo(@PathVariable String
// // tripId,
// // @RequestBody List<String> stockIds) {
// // try {
// // List<TodoDTO> todoDTOs = tripService.createPresetTodo(tripId, stockIds);
// // return
// // ResponseEntity.status(HttpStatus.SEE_OTHER).location(Utils.getLocation(new
// // Object())).body(todoDTOs);
// // } catch (HttpClientErrorException e) {
// // throw e;
// // }
// // }

// }
