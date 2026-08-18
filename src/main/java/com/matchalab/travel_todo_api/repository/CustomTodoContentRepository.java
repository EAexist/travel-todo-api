package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Todo.CustomTodoContent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomTodoContentRepository extends JpaRepository<CustomTodoContent, UUID> {}
