package com.matchalab.trip_todo_api.model.Todo;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matchalab.trip_todo_api.model.Icon;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomTodoContent extends TodoContent {

    @JsonIgnore
    @Nullable
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FlightTodoContent flightTodoContent = null;

    public CustomTodoContent(TodoContent todoContent) {
        super(todoContent);
    }

    @Builder
    public CustomTodoContent(
            UUID id,
            Boolean isStock,
            String category,
            String type,
            String title,
            Icon icon, FlightTodoContent flightTodoContent) {
        super(id, isStock,
                category,
                type,
                title,
                icon);
        this.flightTodoContent = flightTodoContent;
    }
}