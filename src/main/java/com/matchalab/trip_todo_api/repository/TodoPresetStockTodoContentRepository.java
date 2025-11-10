package com.matchalab.trip_todo_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.trip_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.TodoPresetStockTodoContentId;

public interface TodoPresetStockTodoContentRepository
        extends JpaRepository<TodoPresetStockTodoContent, TodoPresetStockTodoContentId> {
}
