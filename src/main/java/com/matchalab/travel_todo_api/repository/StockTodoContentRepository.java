package com.matchalab.travel_todo_api.repository;

import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTodoContentRepository extends JpaRepository<StockTodoContent, UUID> {

  Optional<StockTodoContent> findByTitle(String title);

  Optional<StockTodoContent> findByType(String title);
}
