package com.matchalab.travel_todo_api.model.Todo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matchalab.travel_todo_api.enums.TodoPresetType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TodoPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private TodoPresetType type;

    private String title;

    @OneToMany(mappedBy = "todoPreset", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TodoPresetStockTodoContent> todoPresetStockTodoContents = new ArrayList<TodoPresetStockTodoContent>();
}