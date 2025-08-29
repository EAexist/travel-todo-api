package com.matchalab.trip_todo_api.model.Todo;

import com.matchalab.trip_todo_api.model.Icon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class CustomTodoContent extends TodoContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // @OneToOne
    // @JoinColumn(name = "todo_id", referencedColumnName = "id")
    // private Todo todo;

    public CustomTodoContent(Todo todo, Long id,
            String category,
            String type,
            String title,
            Icon icon) {
        super(category, type, title, icon);
        this.id = id;
    }

    public CustomTodoContent(Todo todo, String category, String type) {
        super(category, type);
        // this.todo = todo;
    }

    public CustomTodoContent(CustomTodoContent customTodoContent) {
        super(customTodoContent.getCategory(), customTodoContent.getType(),
                customTodoContent.getTitle(), customTodoContent.getIcon());
    }
}