package com.matchalab.trip_todo_api.model.Todo;

import java.util.UUID;

import com.matchalab.trip_todo_api.model.Icon;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockTodoContent extends TodoContent {
    // @Nullable
    // @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private FlightTodoContent flightTodoContent = null;

    // @OneToMany(mappedBy = "stockTodoContent", cascade = CascadeType.ALL)
    // private List<TodoPresetStockTodoContent> todoPresetStockTodoContent = new
    // ArrayList<TodoPresetStockTodoContent>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    public StockTodoContent(TodoContent todoContent) {
        super(todoContent);
        // this.flightTodoContent = todoContent.getFlightTodoContent();
    }

    @Builder
    public StockTodoContent(
            UUID id,
            Boolean isStock,
            String category,
            String type,
            String title,
            Icon icon) {
        super(id, isStock,
                category,
                type,
                title,
                icon);
    }
}