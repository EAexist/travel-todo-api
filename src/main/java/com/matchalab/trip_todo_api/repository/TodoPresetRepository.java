package com.matchalab.trip_todo_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Todo.TodoPreset;

public interface TodoPresetRepository extends JpaRepository<TodoPreset, String> {
    Optional<TodoPreset> findByTitle(String title);
}
