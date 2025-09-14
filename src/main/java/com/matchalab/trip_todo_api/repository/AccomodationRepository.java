package com.matchalab.trip_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Accomodation;

public interface AccomodationRepository extends JpaRepository<Accomodation, String> {
}
