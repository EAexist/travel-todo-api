package com.matchalab.trip_todo_api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.DTO.CreateReservationDTO;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.service.HtmlParserService;
import com.matchalab.trip_todo_api.service.ReservationService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user/{userId}/trip/{tripId}/reservation")
public class ReservationController {

    @Autowired
    private final ReservationService reservationService;

    @Autowired
    private final HtmlParserService htmlParserService;

    /**
     * Provide the details of a Trip with the given id.
     */
    @GetMapping(value = "/")
    public ResponseEntity<List<Reservation>> getReservation(@PathVariable String tripId) {
        try {
            return ResponseEntity.ok().body(reservationService.getReservation(tripId));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<List<Reservation>> createReservationFromText(
            @PathVariable String tripId,
            @RequestBody CreateReservationDTO createReservationDTO) {
        try {
            String parsedConfirmationText = htmlParserService
                    .extractTextAndLink(createReservationDTO.confirmationText());

            ReservationCategory category;
            try {
                category = createReservationDTO.category() != null
                        ? ReservationCategory.valueOf(createReservationDTO.category())
                        : ReservationCategory.UNKNOWN;
            } catch (IllegalArgumentException e) {
                category = ReservationCategory.UNKNOWN;
            }

            List<Reservation> reservations = reservationService.extractReservationFromText(
                    parsedConfirmationText, category);

            log.info("[createReservationFromText] reservations extracted:\n" + Utils.asJsonString(reservations));
            reservations = reservationService.saveReservation(tripId, reservations);
            log.info("[createReservationFromText] reservations saved:\n" + Utils.asJsonString(reservations));
            return ResponseEntity.created(Utils.getLocation(reservations.getFirst().getId()))
                    .body(reservations);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    @PatchMapping(value = "/{reservationId}")
    public ResponseEntity<Reservation> setLocalAppStorageFileUri(@PathVariable String tripId,
            @PathVariable String reservationId, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok().body(reservationService.setLocalAppStorageFileUri(tripId, reservationId,
                    body.get("localAppStorageFileUri")));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    // @PostMapping(value = "/", params = "images")
    // public ResponseEntity<ReservationImageAnalysisResult>
    // createReservationFromImage(@PathVariable String tripId,
    // @RequestParam("image") List<MultipartFile> files, @RequestParam
    // ReservationCategory reservationType) {
    // try {
    // return ResponseEntity.ok().body(
    // reservationService.saveImageAnalysisResult(tripId,
    // reservationService.analyzeReservationScreenImage(
    // files, reservationType)));
    // } catch (HttpClientErrorException e) {
    // throw e;
    // }
    // }

    /**
     * Provide the details of a Trip with the given id.
     */
    // @PostMapping(value = "/flight")
    // public ResponseEntity<List<Reservation>>
    // createFlightTicketReservationFromImage(@PathVariable String tripId,
    // @RequestParam("image") List<MultipartFile> files) {
    // try {
    // return
    // ResponseEntity.created(null).body(reservationService.analyzeFlightTicketAndCreateReservation(tripId,
    // files));
    // } catch (HttpClientErrorException e) {
    // throw e;
    // }
    // }

}
