package com.matchalab.trip_todo_api.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.service.TodoService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("user/{userId}/trip/{tripId}/todo")
@Slf4j
public class TodoController {

    @Autowired
    private final TodoService todoService;

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping("")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable UUID tripId, @RequestBody TodoDTO requestbody) {
        try {
            log.info(String.format("[TodoController.createTodo] %s %s", tripId, Utils.asJsonString(requestbody)));
            TodoDTO todoDTO = todoService.createTodo(tripId, requestbody);
            return ResponseEntity.created(Utils.getLocation(todoDTO.id())).body(todoDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    @PatchMapping("/{todoId}")
    public ResponseEntity<TodoDTO> patchTodo(@PathVariable UUID todoId, @RequestBody TodoDTO newTodoDTO) {
        try {
            TodoDTO todoDTO = todoService.patchTodo(todoId, newTodoDTO);
            return ResponseEntity.ok().body(todoDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID todoId) {
        try {
            todoService.deleteTodo(todoId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }
}
