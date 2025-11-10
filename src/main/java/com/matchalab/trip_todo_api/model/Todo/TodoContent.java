package com.matchalab.trip_todo_api.model.Todo;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.enums.TodoCategory;
import com.matchalab.trip_todo_api.model.Icon;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private TodoCategory category;
    private String title;
    private String subtitle;

    @JdbcTypeCode(SqlTypes.JSON)
    private Icon icon;

    public TodoContent(TodoCategory category, String type) {
        this.category = category;
    }

    public TodoContent(TodoContent todoContent) {
        this.category = todoContent.getCategory();
        this.title = todoContent.getTitle();
        this.subtitle = todoContent.getSubtitle();
        this.icon = todoContent.getIcon();
    }
}