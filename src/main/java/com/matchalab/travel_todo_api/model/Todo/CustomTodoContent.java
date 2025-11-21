package com.matchalab.travel_todo_api.model.Todo;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matchalab.travel_todo_api.enums.TodoCategory;
import com.matchalab.travel_todo_api.model.Icon;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @Id
    private UUID id = UUID.randomUUID();

    private String type;

    @JsonIgnore
    @Nullable
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FlightTodoContent flightTodoContent = null;

    public CustomTodoContent(TodoContent todoContent) {
        super(todoContent);
    }

    @Builder
    public CustomTodoContent(
            TodoCategory category,
            String title,
            String subtitle,
            Icon icon,
            UUID id,
            String type, FlightTodoContent flightTodoContent) {
        super(category,
                title,
                subtitle,
                icon);
        this.id = id;
        this.type = type;
        this.flightTodoContent = flightTodoContent;
    }
}