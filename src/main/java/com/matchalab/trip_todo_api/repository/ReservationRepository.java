package com.matchalab.trip_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Reservation.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
}
