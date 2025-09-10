package com.matchalab.trip_todo_api.model.DTO;

import com.matchalab.trip_todo_api.model.Icon;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.FlightTodoContent;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TodoContentDTO {

    @Nullable
    private Long id;

    @Builder.Default
    private Boolean isStock = false;

    private String category;
    private String type;
    private String title;
    private Icon icon;

    @Builder.Default
    private FlightTodoContent flightTodoContent = null;

    public TodoContentDTO(StockTodoContent todoContent) {
        this.id = todoContent.getId();
        this.isStock = todoContent.getIsStock();
        this.category = todoContent.getCategory();
        this.type = todoContent.getType();
        this.title = todoContent.getTitle();
        this.icon = todoContent.getIcon();
    }

    public TodoContentDTO(CustomTodoContent todoContent) {
        this.id = todoContent.getId();
        this.isStock = todoContent.getIsStock();
        this.category = todoContent.getCategory();
        this.type = todoContent.getType();
        this.title = todoContent.getTitle();
        this.icon = todoContent.getIcon();
        this.flightTodoContent = todoContent.getFlightTodoContent();
    }

    // public CustomTodoContent toCustomTodoContent() {
    // return this.isStock ? null : new CustomTodoContent();
    // }
}