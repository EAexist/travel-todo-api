package com.matchalab.trip_todo_api.model.Todo;

import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import com.matchalab.trip_todo_api.model.Icon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
    @Id
    @Column(updatable = false, nullable = false)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Column(unique = true)
    private String type;

    public StockTodoContent(TodoContent todoContent) {
        super(todoContent);
        // this.flightTodoContent = todoContent.getFlightTodoContent();
    }

    @Builder
    public StockTodoContent(
            UUID id,
            String category,
            String type,
            String title,
            Icon icon) {
        super(
                category,
                title,
                icon);
        this.id = id;
        this.type = type;
    }
}