package com.matchalab.trip_todo_api.model.Todo;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.matchalab.trip_todo_api.model.Icon;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class TodoContent {

    private String category;
    private String type;
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    private Icon icon;

    public TodoContent(String category, String type) {
        this.category = category;
        this.type = type;
    }
}