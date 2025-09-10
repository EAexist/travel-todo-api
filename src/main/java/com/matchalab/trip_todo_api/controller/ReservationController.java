package com.matchalab.trip_todo_api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import com.matchalab.trip_todo_api.enums.ReservationType;
import com.matchalab.trip_todo_api.model.DTO.ReservationImageAnalysisResult;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.service.ReservationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("user/{userId}/trip/{tripId}/reservation")
public class ReservationController {

    @Autowired
    private final ReservationService reservationService;

    /**
     * Provide the details of a Trip with the given id.
     */
    @GetMapping(value = "/")
    public ResponseEntity<List<Reservation>> getReservation(@PathVariable Long tripId) {
        try {
            return ResponseEntity.ok().body(reservationService.getReservation(tripId));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    @PatchMapping(value = "/{reservationId}")
    public ResponseEntity<Reservation> setLocalAppStorageFileUri(@PathVariable Long tripId,
            @PathVariable Long reservationId, @RequestBody Map<String, String> body) {
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
    @PostMapping(value = "/", params = "images")
    public ResponseEntity<ReservationImageAnalysisResult> createReservationFromImage(@PathVariable Long tripId,
            @RequestParam("image") List<MultipartFile> files, @RequestParam ReservationType reservationType) {
        try {
            return ResponseEntity.ok().body(
                    reservationService.saveImageAnalysisResult(tripId, reservationService.analyzeReservationScreenImage(
                            files, reservationType)));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    @PostMapping(value = "/flight")
    public ResponseEntity<List<Reservation>> createFlightTicketReservationFromImage(@PathVariable Long tripId,
            @RequestParam("image") List<MultipartFile> files) {
        try {
            return ResponseEntity.created(null).body(reservationService.analyzeFlightTicketAndCreateReservation(tripId,
                    files));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    @PostMapping(value = "", params = "text")
    public ResponseEntity<ReservationImageAnalysisResult> createReservationFromText(@PathVariable Long tripId,
            @RequestParam("text") String text) {
        try {
            return ResponseEntity.ok().body(reservationService.saveImageAnalysisResult(tripId,
                    reservationService.uploadReservationText(text)));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

}
