package com.matchalab.trip_todo_api.model.Todo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todoPreset_stockTodoContent") // Good practice to explicitly name the join table
@Builder
public class TodoPresetStockTodoContent {

    @EmbeddedId
    @Builder.Default
    private TodoPresetStockTodoContentId id = new TodoPresetStockTodoContentId();

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId("stockTodoContentId") // Maps the stockTodoContentId field of the composite key
    @JoinColumn(name = "stock-todo-content_id")
    private StockTodoContent stockTodoContent;

    @Builder.Default
    private Boolean isFlaggedToAdd = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("todoPresetId")
    @JoinColumn(name = "todo-preset_id")
    private TodoPreset todoPreset;

    public TodoPresetStockTodoContent(TodoPreset todoPreset, StockTodoContent stockTodoContent,
            Boolean isFlaggedToAdd) {
        this.todoPreset = todoPreset;
        this.stockTodoContent = stockTodoContent;
        this.isFlaggedToAdd = isFlaggedToAdd;
        this.id = new TodoPresetStockTodoContentId(todoPreset.getId(), stockTodoContent.getId());
    }
}