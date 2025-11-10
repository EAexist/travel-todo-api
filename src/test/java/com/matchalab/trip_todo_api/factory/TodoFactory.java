package com.matchalab.trip_todo_api.factory;

import java.util.UUID;

import com.matchalab.trip_todo_api.DTO.TodoContentDTO;
import com.matchalab.trip_todo_api.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;

public class TodoFactory {
    public static Todo createValidCustomTodo(String key) {
        switch (key) {
            case "new-reservation":
                return Todo.builder().id(UUID.nameUUIDFromBytes(key.getBytes())).orderKey(0)
                        .customTodoContent(CustomTodoContent.builder().id(UUID.nameUUIDFromBytes(key.getBytes()))
                                .category("reservation").title("새 예약")
                                .isStock(false)
                                .build())
                        .build();
            default:
                return Todo.builder().id(UUID.randomUUID()).orderKey(0)
                        .customTodoContent(CustomTodoContent.builder().id(UUID.randomUUID())
                                .build())
                        .build();
        }
    }

    public static TodoDTO createValidCustomTodoDTO(String key) {
        switch (key) {
            case "new-reservation":
                return TodoDTO.builder().id(UUID.nameUUIDFromBytes(key.getBytes())).orderKey(0)
                        .content(TodoContentDTO.builder().id(UUID.nameUUIDFromBytes(key.getBytes()))
                                .category("reservation").title("새 예약")
                                .isStock(false)
                                .build())
                        .build();
            default:
                return TodoDTO.builder().id(UUID.randomUUID()).orderKey(0)
                        .content(TodoContentDTO.builder().id(UUID.randomUUID())
                                .build())
                        .build();
        }
    }

    public static Todo createValidStockTodo(String key, StockTodoContent stockTodoContent) {
        switch (key) {
            case "currency":
                return Todo.builder().id(UUID.nameUUIDFromBytes(key.getBytes())).orderKey(0)
                        .stockTodoContent(stockTodoContent)
                        .build();
            default:
                return Todo.builder().id(UUID.nameUUIDFromBytes(key.getBytes())).orderKey(0)
                        .stockTodoContent(stockTodoContent)
                        .build();
        }
    }

    // public static TodoDTO createValidStockTodoDTO(String key) {
    // switch (key) {
    // case "new-reservation":
    // return
    // TodoDTO.builder().id(UUID.nameUUIDFromBytes(key.getBytes())).orderKey(0)
    // .content(TodoContentDTO.builder().id(UUID.nameUUIDFromBytes(key.getBytes()))
    // .category("reservation").title("새 예약")
    // .isStock(false)
    // .build())
    // .build();
    // default:
    // return
    // TodoDTO.builder().id(UUID.nameUUIDFromBytes(key.getBytes())).orderKey(0)
    // .content(TodoContentDTO.builder().id(UUID.nameUUIDFromBytes(key.getBytes()))
    // .category("reservation").title("새 예약")
    // .isStock(false)
    // .build())
    // .build();
    // }
    // }
}
