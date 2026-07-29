package com.matchalab.travel_todo_api.factory;

import com.matchalab.travel_todo_api.DTO.TodoCreateDTO;
import com.matchalab.travel_todo_api.DTO.TodoPatchDTO;
import java.util.UUID;

import com.matchalab.travel_todo_api.DTO.TodoContentDTO;
import com.matchalab.travel_todo_api.DTO.TodoDTO;
import com.matchalab.travel_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;
import com.matchalab.travel_todo_api.model.Todo.Todo;

public class TodoFactory {

    public static Todo createValidCustomTodo() {
        return Todo.builder().id(UUID.randomUUID()).orderKey(0)
                .customTodoContent(CustomTodoContent.builder().id(UUID.randomUUID())
                        .build())
                .build();
    }

//    public static Todo createValidCustomTodo(String key) {
//        switch (key) {
//            default:
//                return Todo.builder().id(UUID.randomUUID()).orderKey(0)
//                        .customTodoContent(CustomTodoContent.builder().id(UUID.randomUUID())
//                                .build())
//                        .build();
//        }
//    }

    public static TodoDTO createValidCustomTodoDTO() {
                return TodoDTO.builder().id(UUID.randomUUID()).orderKey(0)
                        .content(TodoContentDTO.builder().id(UUID.randomUUID())
                                .build())
                        .build();
    }

    public static TodoCreateDTO createValidCustomTodoCreateDTO() {
        return TodoCreateDTO.builder().id(UUID.randomUUID()).orderKey(0)
            .content(TodoContentDTO.builder().id(UUID.randomUUID())
                .build())
            .build();
    }

    public static TodoPatchDTO createValidCustomTodoPatchDTO() {
        return TodoPatchDTO.builder().id(UUID.randomUUID()).orderKey(0)
            .content(TodoContentDTO.builder().id(UUID.randomUUID())
                .build())
            .build();
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
