package com.matchalab.travel_todo_api.reservation_analysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreateReservationJobRepository extends JpaRepository<CreateReservationJob, UUID> {}
