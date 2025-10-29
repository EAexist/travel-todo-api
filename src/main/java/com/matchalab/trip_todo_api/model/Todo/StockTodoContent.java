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
    @Id
    @Column(updatable = false, nullable = false)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(unique = true)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    public StockTodoContent(TodoContent todoContent) {
        super(todoContent);
        // this.flightTodoContent = todoContent.getFlightTodoContent();
    }

    @Builder
    public StockTodoContent(
            String category,
            String title,
            String tip,
            Icon icon,
            UUID id,
            String type) {
        super(
                category,
                title,
                tip,
                icon);
        this.id = id;
        this.type = type;
    }
}