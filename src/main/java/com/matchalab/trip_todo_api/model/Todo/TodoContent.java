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

    private String category;
    private String title;
    private String tip;

    @JdbcTypeCode(SqlTypes.JSON)
    private Icon icon;

    public TodoContent(String category, String type) {
        this.category = category;
    }

    public TodoContent(TodoContent todoContent) {
        this.category = todoContent.getCategory();
        this.title = todoContent.getTitle();
        this.tip = todoContent.getTip();
        this.icon = todoContent.getIcon();
    }
}