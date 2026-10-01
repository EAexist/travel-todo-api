package com.matchalab.travel_todo_api.repository;


import com.matchalab.travel_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.travel_todo_api.model.Todo.TodoPresetStockTodoContentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoPresetStockTodoContentRepository
    extends JpaRepository<TodoPresetStockTodoContent, TodoPresetStockTodoContentId> {}
