package com.matchalab.trip_todo_api.model.Todo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class TodoPresetStockTodoContentId implements Serializable {

    @JoinColumn(name = "todo-preset_id")
    private UUID todoPresetId;

    @JoinColumn(name = "stock-todo-content_id")
    private UUID stockTodoContentId;

    public TodoPresetStockTodoContentId(UUID todoPresetId, UUID stockTodoContentId) {
        this.todoPresetId = todoPresetId;
        this.stockTodoContentId = stockTodoContentId;
    }

    // Getters and setters
    // IMPORTANT: Override equals() and hashCode()
    // This is crucial for correctly identifying and managing the composite key
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TodoPresetStockTodoContentId that = (TodoPresetStockTodoContentId) o;
        return Objects.equals(todoPresetId, that.todoPresetId) &&
                Objects.equals(stockTodoContentId, that.stockTodoContentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoPresetId, stockTodoContentId);
    }
}
