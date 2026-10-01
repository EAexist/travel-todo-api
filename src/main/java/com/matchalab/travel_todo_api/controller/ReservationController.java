package com.matchalab.travel_todo_api.controller;

import com.matchalab.travel_todo_api.DTO.CreateReservationDTO;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.model.Reservation.Reservation;
import com.matchalab.travel_todo_api.model.Reservation.ReservationDTO;
import com.matchalab.travel_todo_api.model.Reservation.ReservationPatchDTO;
import com.matchalab.travel_todo_api.service.HtmlParserService;
import com.matchalab.travel_todo_api.service.ReservationService;
import com.matchalab.travel_todo_api.utils.Utils;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class ReservationController {

  @Autowired private final ReservationService reservationService;

  @Autowired private final HtmlParserService htmlParserService;

  @PostMapping("trip/{tripId}/reservation")
  public ResponseEntity<ReservationDTO> createReservation(
      @PathVariable UUID tripId, @RequestBody ReservationPatchDTO requestbody) {
      ReservationDTO reservationDTO = reservationService.createReservation(tripId, requestbody);
      return ResponseEntity.created(Utils.getLocation(reservationDTO.getId())).body(reservationDTO);
  }

  @PostMapping("trip/{tripId}/reservation/analysis/text")
  public ResponseEntity<Void> createReservationFromText(
      @PathVariable UUID tripId, @RequestBody CreateReservationDTO createReservationDTO) throws Exception {

      reservationService.createReservationFromText(
          tripId,
          createReservationDTO
      );

      return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  /** Provide the details of an Trip with the given id. */
  @PatchMapping("reservation/{reservationId}")
  public ResponseEntity<ReservationDTO> patchReservation(
      @PathVariable UUID reservationId, @RequestBody ReservationPatchDTO newReservationDTO) {
      ReservationDTO reservationDTO =
          reservationService.patchReservation(reservationId, newReservationDTO);
      return ResponseEntity.ok().body(reservationDTO);
  }

  /** Provide the details of an Trip with the given id. */
  @DeleteMapping("reservation/{reservationId}")
  public ResponseEntity<Void> deleteReservation(@PathVariable UUID reservationId) {
      reservationService.deleteReservation(reservationId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }


    @GetMapping("reservations")
    public ResponseEntity<List<ReservationDTO>> getReservations(
            @RequestParam List<UUID> ids) {
        List<ReservationDTO> reservationDTOs = reservationService.getReservations(ids);
        return ResponseEntity.ok().body(reservationDTOs);
    }

    @PostMapping("trip/{tripId}/reservation/batch")
    public ResponseEntity<List<ReservationDTO>> createReservations(
            @PathVariable UUID tripId, @RequestBody List<ReservationPatchDTO> requestBodies) {
        List<ReservationDTO> reservationDTOs = reservationService.createReservationBatch(tripId, requestBodies);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDTOs);
    }
  /** Provide the details of a Trip with the given id. */
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

  /** Provide the details of a Trip with the given id. */
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

  /** Provide the details of a Trip with the given id. */
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
