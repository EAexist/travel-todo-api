package com.matchalab.trip_todo_api.model.Todo;

import com.matchalab.trip_todo_api.model.Icon;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresetTodoContent extends TodoContent {

    @Id
    private Long id;

    // @OneToMany(mappedBy = "presetTodoContent", cascade = CascadeType.ALL,
    // orphanRemoval = true)
    // private List<Todo> todo;

    public PresetTodoContent(Long id,
            String category,
            String type,
            String title,
            Icon icon) {
        super(category, type, title, icon);
        this.id = id;
    }

    public PresetTodoContent(PresetTodoContent presetTodoContent) {
        super(presetTodoContent.getCategory(), presetTodoContent.getType(),
                presetTodoContent.getTitle(), presetTodoContent.getIcon());
        this.id = presetTodoContent.getId();
    }
}