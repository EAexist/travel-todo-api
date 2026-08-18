package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Accomodation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccomodationRepository extends JpaRepository<Accomodation, UUID> {}
