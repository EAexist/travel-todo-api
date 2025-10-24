package com.matchalab.trip_todo_api.model.Todo;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.model.Icon;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Builder(builderMethodName = "todoContentBuilder")
public class TodoContent {

    // @Id
    // @Builder.Default
    // private UUID id = UUID.randomUUID();

    @Builder.Default
    private Boolean isStock = false;

    private String category;
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    private Icon icon;

    // @Nullable
    // @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @Builder.Default
    // private FlightTodoContent flightTodoContent = null;

    public TodoContent(String category, String type) {
        this.category = category;
        // this.type = type;
    }

    public TodoContent(TodoContent todoContent) {
        this.isStock = todoContent.getIsStock();
        this.category = todoContent.getCategory();
        // this.type = todoContent.getType();
        this.title = todoContent.getTitle();
        this.icon = todoContent.getIcon();
        // this.flightTodoContent = todoContent.getFlightTodoContent();
    }
}