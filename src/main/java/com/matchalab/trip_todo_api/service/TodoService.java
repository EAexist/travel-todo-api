package com.matchalab.trip_todo_api.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.exception.TripNotFoundException;
import com.matchalab.trip_todo_api.mapper.TodoMapper;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.DTO.TodoDTO;
import com.matchalab.trip_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.repository.CustomTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TodoRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {
    @Autowired
    private final TripRepository tripRepository;
    @Autowired
    private final TodoRepository todoRepository;
    @Autowired
    private final CustomTodoContentRepository customTodoContentRepository;
    @Autowired
    private final TodoMapper todoMapper;

    /**
     * Create new todo.
     */
    @Transactional
    public TodoDTO createTodo(UUID tripId, TodoDTO todoDTO) {
        Todo newTodo = todoMapper.mapToTodo(todoDTO);
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(newTodo)));

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        trip.addTodo(newTodo);
        tripRepository.save(trip);
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(newTodo)));

        return todoMapper.mapToTodoDTO(newTodo);
    }

    /**
     * Change contents or orderKey of todo.
     */
    public TodoDTO patchTodo(UUID todoId, TodoDTO newTodoDTO) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new NotFoundException(todoId));
        Todo updatedTodo = todoMapper.updateTodoFromDto(newTodoDTO, todo);
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(updatedTodo)));

        TodoDTO todoDTO = todoMapper.mapToTodoDTO(todoRepository.save(updatedTodo));
        log.info(Utils.asJsonString(todoDTO));
        return todoDTO;
    }

    /**
     * Create new todo.
     */
    public void deleteTodo(UUID todoId) {
        todoRepository.findById(todoId).ifPresentOrElse(entity -> todoRepository.delete(entity),
                () -> new NotFoundException(todoId));
    }

}
