package com.matchalab.trip_todo_api.controller;

import java.util.List;
import java.util.Map;
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

import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.model.DTO.CreateReservationDTO;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.Reservation.ReservationDTO;
import com.matchalab.trip_todo_api.service.HtmlParserService;
import com.matchalab.trip_todo_api.service.ReservationService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class ReservationController {

    @Autowired
    private final ReservationService reservationService;

    @Autowired
    private final HtmlParserService htmlParserService;

    /**
     * Provide the details of a Trip with the given id.
     */
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getReservation(@PathVariable UUID tripId) {
        try {
            return ResponseEntity.ok().body(reservationService.getReservation(tripId));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    @PostMapping("trip/{tripId}/reservation")
    public ResponseEntity<ReservationDTO> createReservation(
            @PathVariable UUID tripId, @RequestBody ReservationDTO requestbody) {
        try {
            ReservationDTO reservationDTO = reservationService.createReservation(tripId, requestbody);
            return ResponseEntity.created(Utils.getLocation(reservationDTO.getId())).body(reservationDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    @PostMapping("trip/{tripId}/reservation/analysis/text")
    public ResponseEntity<List<ReservationDTO>> createReservationFromText(
            @PathVariable UUID userId,
            @PathVariable UUID tripId,
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

            List<ReservationDTO> reservationDTOs = reservationService.saveReservation(tripId, reservations);
            log.info("[createReservationFromText] reservations saved:\n" + Utils.asJsonString(reservationDTOs));
            return ResponseEntity
                    .created(ServletUriComponentsBuilder.fromCurrentRequestUri()
                            .replacePath("/trip/{tripId}/reservation/{reservationId}")
                            .buildAndExpand(userId, tripId, reservationDTOs.getFirst().getId())
                            .toUri())
                    .body(reservationDTOs);
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
     * Provide the details of an Trip with the given id.
     */
    @PatchMapping("reservation/{reservationId}")
    public ResponseEntity<ReservationDTO> patchReservation(@PathVariable UUID reservationId,
            @RequestBody ReservationDTO newReservationDTO) {
        try {
            ReservationDTO reservationDTO = reservationService.patchReservation(reservationId, newReservationDTO);
            return ResponseEntity.ok().body(reservationDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    @DeleteMapping("reservation/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID reservationId) {
        try {
            reservationService.deleteReservation(reservationId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    // @PatchMapping(value = "/{reservationId}")
    // public ResponseEntity<Reservation> setLocalAppStorageFileUri(@PathVariable
    // UUID tripId,
    // @PathVariable UUID reservationId, @RequestBody Map<String, String> body) {
    // try {
    // return
    // ResponseEntity.ok().body(reservationService.setLocalAppStorageFileUri(tripId,
    // reservationId,
    // body.get("localAppStorageFileUri")));
    // } catch (HttpClientErrorException e) {
    // throw e;
    // }
    // }

    /**
     * Provide the details of a Trip with the given id.
     */
    // @PostMapping(value = "/", params = "images")
    // public ResponseEntity<ReservationImageAnalysisResult>
    // createReservationFromImage(@PathVariable UUID tripId,
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
    // createFlightTicketReservationFromImage(@PathVariable UUID tripId,
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
