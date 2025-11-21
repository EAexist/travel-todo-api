package com.matchalab.travel_todo_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.travel_todo_api.model.Accomodation;

public interface AccomodationRepository extends JpaRepository<Accomodation, UUID> {
}
