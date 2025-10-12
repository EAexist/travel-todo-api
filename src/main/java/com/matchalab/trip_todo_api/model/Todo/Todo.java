package com.matchalab.trip_todo_api.model.Todo;

import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.Flight.Flight;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String note;
    private String completeDateIsoString;
    private int orderKey;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "trip_id")
    // private Trip trip;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Nullable
    private CustomTodoContent customTodoContent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preset-todo-content_id")
    @Nullable
    private StockTodoContent stockTodoContent;

    public Todo(Todo todo) {
        this.note = todo.getNote();
        this.completeDateIsoString = todo.getCompleteDateIsoString();
        this.orderKey = todo.getOrderKey();
        this.customTodoContent = todo.getCustomTodoContent() != null
                ? new CustomTodoContent(todo.getCustomTodoContent())
                : null;
        this.stockTodoContent = todo.getStockTodoContent() != null ? new StockTodoContent(todo.getStockTodoContent())
                : null;
    }
}
