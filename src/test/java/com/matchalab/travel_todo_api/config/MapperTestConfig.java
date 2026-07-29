package com.matchalab.travel_todo_api.config;

import com.matchalab.travel_todo_api.DTO.TodoDTO;
import com.matchalab.travel_todo_api.DTO.TodoPatchDTO;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@TestConfiguration
@ComponentScan(basePackages = {
    "com.matchalab.travel_todo_api.mapper",
    "com.matchalab.travel_todo_api.DTO",
})
public class MapperTestConfig {

    @Autowired
    private TodoDTO stockTodoDTO;

    @Autowired
    private TodoDTO customTodoDTO;

    @Bean
    public TodoPatchDTO stockTodoPatchDTO() {
        return TodoPatchDTO.builder()
                .id(stockTodoDTO.id())
                .orderKey(stockTodoDTO.orderKey())
                .note(stockTodoDTO.note())
                .completeDateIsoString(JsonNullable.of(stockTodoDTO.completeDateIsoString()))
                .content(stockTodoDTO.content())
                .build();
    }

    @Bean
    public TodoPatchDTO customTodoPatchDTO() {
        return TodoPatchDTO.builder()
                .id(customTodoDTO.id())
                .orderKey(customTodoDTO.orderKey())
                .note(customTodoDTO.note())
                .completeDateIsoString(JsonNullable.of(customTodoDTO.completeDateIsoString()))
                .content(customTodoDTO.content())
                .build();
    }
}
