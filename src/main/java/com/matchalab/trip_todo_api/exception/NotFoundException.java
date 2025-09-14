package com.matchalab.trip_todo_api.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String id) {
        super("Could not find  " + id);
    }
}
