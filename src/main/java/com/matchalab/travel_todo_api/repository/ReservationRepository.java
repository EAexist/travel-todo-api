package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Reservation.Reservation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

//    @EntityGraph(attributePaths = {
//            "visitJapan",
//            "accomodation",
//            "flightBooking",
//            "flightTicket",
//            "generalReservation"
//    })
    List<Reservation> findAllById(Iterable<UUID> ids);
}
