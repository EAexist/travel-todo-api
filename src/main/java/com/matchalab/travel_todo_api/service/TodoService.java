package com.matchalab.travel_todo_api.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchalab.travel_todo_api.DTO.TodoDTO;
import com.matchalab.travel_todo_api.DTO.TodoPatchDTO;
import com.matchalab.travel_todo_api.exception.NotFoundException;
import com.matchalab.travel_todo_api.exception.TripNotFoundException;
import com.matchalab.travel_todo_api.mapper.TodoMapper;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.Reservation.Reservation;
import com.matchalab.travel_todo_api.model.Todo.Todo;
import com.matchalab.travel_todo_api.repository.CustomTodoContentRepository;
import com.matchalab.travel_todo_api.repository.TodoRepository;
import com.matchalab.travel_todo_api.repository.TripRepository;
import com.matchalab.travel_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    /*
     * Repository
     */
    @Autowired
    private final TripRepository tripRepository;
    @Autowired
    private final TodoRepository todoRepository;

    /*
     * Mapper
     */
    @Autowired
    private final TodoMapper todoMapper;

    /**
     * Create new todo.
     */
    @Transactional
    public TodoDTO createTodo(UUID tripId, TodoPatchDTO todoDTO) {
        Todo newTodo = todoMapper.mapToTodo(todoDTO);
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(newTodo)));

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        trip.addTodo(newTodo);
        tripRepository.save(trip);
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(newTodo)));

        return todoMapper.mapToTodoDTO(newTodo);
    }

    /**
     * Change contents/orderKey of todo.
     */
    public TodoDTO patchTodo(UUID todoId, TodoPatchDTO newTodoDTO) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new NotFoundException(todoId));
        Todo updatedTodo = todoMapper.updateTodoFromDto(newTodoDTO, todo);
        log.info(Utils.asJsonString(todoMapper.mapToTodoDTO(updatedTodo)));

        TodoDTO todoDTO = todoMapper.mapToTodoDTO(todoRepository.save(updatedTodo));
        log.info(Utils.asJsonString(todoDTO));
        return todoDTO;
    }

    /**
     * Delete todo.
     */
    public void deleteTodo(UUID todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException(todoId));
        Trip trip = todo.getTrip();
        trip.removeTodo(todo);
        tripRepository.save(trip);
    }

}
