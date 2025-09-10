package com.matchalab.trip_todo_api.model.Todo;

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
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Builder.Default
    private Boolean isStock = false;

    private String category;
    private String type;
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    private Icon icon;

    // @Nullable
    // @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @Builder.Default
    // private FlightTodoContent flightTodoContent = null;

    public TodoContent(String category, String type) {
        this.category = category;
        this.type = type;
    }

    public TodoContent(TodoContent todoContent) {
        this.isStock = todoContent.getIsStock();
        this.category = todoContent.getCategory();
        this.type = todoContent.getType();
        this.title = todoContent.getTitle();
        this.icon = todoContent.getIcon();
        // this.flightTodoContent = todoContent.getFlightTodoContent();
    }
}