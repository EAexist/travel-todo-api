// package com.matchalab.trip_todo_api.controller;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.client.HttpClientErrorException;

// import com.amadeus.Amadeus;
// import com.amadeus.Params;
// import com.amadeus.exceptions.ResponseException;
// import com.amadeus.resources.Location;

// import lombok.extern.slf4j.Slf4j;

// // @RestController
// // @RequiredArgsConstructor
// // @RequestMapping("/amadeus")
// @Slf4j
// public class AmadeusController {

// private Amadeus amadeus;

// public AmadeusController(@Value("${amadeus.client-id}") String
// AMADEUS_CLIENT_ID,
// @Value("${amadeus.client_secret}") String AMADEUS_CLIENT_SECRET) {
// log.info(String.format("[AMADEUS_CLIENT_ID] %s", AMADEUS_CLIENT_ID));
// log.info(String.format("[AMADEUS_CLIENT_SECRET] %s", AMADEUS_CLIENT_SECRET));
// amadeus = Amadeus.builder(AMADEUS_CLIENT_ID, AMADEUS_CLIENT_SECRET).build();
// }

// /**
// * Provide the details of an Trip with the given id.
// */
// @GetMapping("/locations")
// public ResponseEntity<Location[]> googlePlaceAutocomplete(@RequestParam
// String view, @RequestParam String subType,
// @RequestParam String keyword) throws ResponseException {
// try {
// log.info(String.format("[proxy/google-places-autocomplete] subType=%s ,
// keyword=%s", subType, keyword));

// Params params = Params.with("view", view).and("subType",
// subType).and("keyword", keyword);
// Location[] locations = amadeus.referenceData.locations.get(params);
// log.info(locations.toString());
// return ResponseEntity.ok().body(locations);
// } catch (HttpClientErrorException e) {
// throw e;
// }
// }

// }
