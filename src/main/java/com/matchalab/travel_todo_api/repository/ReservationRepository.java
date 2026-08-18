package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Reservation.Reservation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {}
