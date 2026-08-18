package com.matchalab.travel_todo_api.service;

import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.exception.NotFoundException;
import com.matchalab.travel_todo_api.exception.TripNotFoundException;
import com.matchalab.travel_todo_api.mapper.ReservationMapper;
import com.matchalab.travel_todo_api.model.Reservation.Reservation;
import com.matchalab.travel_todo_api.model.Reservation.ReservationDTO;
import com.matchalab.travel_todo_api.model.Reservation.ReservationPatchDTO;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.travel_todo_api.repository.ReservationRepository;
import com.matchalab.travel_todo_api.repository.TripRepository;
import com.matchalab.travel_todo_api.service.ChatModelService.ChatModelService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Setter
@RequiredArgsConstructor
public class ReservationService {

  /*
   * Repository
   */
  @Autowired private final TripRepository tripRepository;

  @Autowired private ReservationRepository reservationRepository;

  /*
   * Service
   */
  @Autowired private ChatModelService chatModelService;

  /*
   * Mapper
   */
  @Autowired private ReservationMapper reservationMapper;

  /** Provide the details of a Trip with the given id. */
  public List<ReservationDTO> getReservation(UUID tripId) {
    List<ReservationDTO> reservation =
        tripRepository
            .findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId))
            .getReservations()
            .stream()
            .map(reservationMapper::mapToDTO)
            .toList();
    return reservation;
  }

  /** Create new todo. */
  @Transactional
  public ReservationDTO createReservation(UUID tripId, ReservationPatchDTO reservationDTO) {
    Reservation reservation = reservationMapper.mapToReservation(reservationDTO);

    Trip trip =
        tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
    trip.addReservation(reservation);
    tripRepository.save(trip);

    return reservationMapper.mapToDTO(reservation);
  }

  /** Change contents/orderKey of reservation. */
  public ReservationDTO patchReservation(
      UUID reservationId, ReservationPatchDTO newReservationDTO) {

    Reservation reservation =
        reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new NotFoundException(reservationId));
    Reservation updatedReservation =
        reservationMapper.updateFromDto(newReservationDTO, reservation);

    ReservationDTO reservationDTO =
        reservationMapper.mapToDTO(reservationRepository.save(updatedReservation));
    return reservationDTO;
  }

  /** Delete reservation. */
  public void deleteReservation(UUID reservationId) {
    Reservation reservation =
        reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new NotFoundException(reservationId));
    Trip trip = reservation.getTrip();
    trip.removeReservation(reservation);
    tripRepository.save(trip);
  }

  @Transactional
  public List<ReservationDTO> saveReservation(UUID tripId, List<Reservation> reservation)
      throws Exception {

    Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException(null));

    Boolean isAdded = trip.addReservation(reservation);

    trip = tripRepository.save(trip);

    if (isAdded) {
      return reservation.stream().map(reservationMapper::mapToDTO).toList();
    } else {
      throw new Exception();
    }
  }

  public List<Reservation> extractReservationFromText(
      String confirmationText, ReservationCategory category) throws Exception {

    List<Reservation> reservation = new ArrayList<Reservation>();

    ExtractReservationChatResultDTO chatResult =
        chatModelService.extractReservationFromText(confirmationText);

    reservation.addAll(
        chatResult.flightBookings().stream().map(reservationMapper::mapToReservation).toList());

    reservation.addAll(
        chatResult.flightTickets().stream().map(reservationMapper::mapToReservation).toList());

    reservation.addAll(
        chatResult.accomodations().stream().map(reservationMapper::mapToReservation).toList());

    reservation.addAll(
        chatResult.otherReservations().stream().map(reservationMapper::mapToReservation).toList());

    reservation.stream()
        .forEach(
            r -> r.setRawText(chatResult.partOfTextAndLinksThatContainsReservationInformation()));

    return reservation;
  }
}
