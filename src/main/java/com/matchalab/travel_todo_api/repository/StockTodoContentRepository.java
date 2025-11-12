package com.matchalab.travel_todo_api.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;

public interface StockTodoContentRepository extends
        JpaRepository<StockTodoContent, UUID> {
    Optional<StockTodoContent> findByTitle(String title);
}
