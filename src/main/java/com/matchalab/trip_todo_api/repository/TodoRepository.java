package com.matchalab.trip_todo_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Todo.Todo;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
}
