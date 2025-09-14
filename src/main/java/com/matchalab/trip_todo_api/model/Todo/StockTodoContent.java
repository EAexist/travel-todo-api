package com.matchalab.trip_todo_api.model.Todo;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.model.Icon;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
// @AllArgsConstructor
@NoArgsConstructor
public class StockTodoContent extends TodoContent {
    // @Nullable
    // @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private FlightTodoContent flightTodoContent = null;

    // @OneToMany(mappedBy = "stockTodoContent", cascade = CascadeType.ALL)
    // private List<TodoPresetStockTodoContent> todoPresetStockTodoContent = new
    // ArrayList<TodoPresetStockTodoContent>();

    public StockTodoContent(TodoContent todoContent) {
        super(todoContent);
        // this.flightTodoContent = todoContent.getFlightTodoContent();
    }

    @Builder
    public StockTodoContent(
            String id,
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