package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.enums.TodoPresetType;
import com.matchalab.travel_todo_api.model.Todo.TodoPreset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoPresetRepository extends JpaRepository<TodoPreset, UUID> {
  Optional<TodoPreset> findByType(TodoPresetType type);

  Optional<TodoPreset> findByTitle(String title);

  Boolean existsByType(TodoPresetType type);
}
