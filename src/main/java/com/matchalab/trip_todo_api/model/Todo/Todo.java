package com.matchalab.trip_todo_api.model.Todo;

import java.util.UUID;

import org.springframework.data.domain.Persistable;

import com.matchalab.trip_todo_api.model.Trip;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
// @AllArgsConstructor
@Builder
public class Todo implements Persistable<UUID> {

    @Id
    // @GeneratedOrCustomUUID)
    private UUID id;

    private String note;
    private String completeDateIsoString;
    private int orderKey;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Nullable
    private CustomTodoContent customTodoContent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preset-todo-content_id")
    @Nullable
    private StockTodoContent stockTodoContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PostPersist
    @PostLoad
    private void setIsNotNew() {
        this.isNew = false;
    }

    public Todo(UUID id,
            String note,
            String completeDateIsoString,
            int orderKey,
            CustomTodoContent customTodoContent,
            StockTodoContent stockTodoContent,
            Trip trip,
            boolean isNew) {
        this.id = id;
        this.note = note;
        this.completeDateIsoString = completeDateIsoString;
        this.orderKey = orderKey;
        this.customTodoContent = customTodoContent != null
                ? customTodoContent
                : null;
        this.stockTodoContent = stockTodoContent != null ? stockTodoContent
                : null;
        this.isNew = true;
    }

    public Todo(Todo todo) {
        this.id = UUID.randomUUID();
        this.note = todo.getNote();
        this.completeDateIsoString = todo.getCompleteDateIsoString();
        this.orderKey = todo.getOrderKey();
        this.customTodoContent = todo.getCustomTodoContent() != null
                ? new CustomTodoContent(todo.getCustomTodoContent())
                : null;
        this.stockTodoContent = todo.getStockTodoContent() != null ? todo.getStockTodoContent()
                : null;
        this.isNew = todo.isNew();
    }
}
