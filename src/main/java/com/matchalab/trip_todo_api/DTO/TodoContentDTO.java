package com.matchalab.trip_todo_api.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matchalab.trip_todo_api.enums.TodoCategory;
import com.matchalab.trip_todo_api.model.Icon;

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
    private UUID id;

    @Builder.Default
    private Boolean isStock = false;

    private TodoCategory category;
    private String type;
    private String title;
    private String subtitle;
    private Icon icon;

    @Builder.Default
    private List<FlightRouteDTO> flightRoutes = new ArrayList<FlightRouteDTO>();

    // public TodoContentDTO(StockTodoContent todoContent) {
    // this.id = todoContent.getId();
    // this.isStock = todoContent.getIsStock();
    // this.category = todoContent.getCategory();
    // this.type = todoContent.getType();
    // this.title = todoContent.getTitle();
    // this.icon = todoContent.getIcon();
    // }

    // public TodoContentDTO(CustomTodoContent todoContent) {
    // this.id = todoContent.getId();
    // this.isStock = todoContent.getIsStock();
    // this.category = todoContent.getCategory();
    // this.type = todoContent.getType();
    // this.title = todoContent.getTitle();
    // this.icon = todoContent.getIcon();
    // this.flightTodoContent = todoContent.getFlightTodoContent();
    // }

    // public CustomTodoContent toCustomTodoContent() {
    // return this.isStock ? null : new CustomTodoContent();
    // }
}