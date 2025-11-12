package com.matchalab.travel_todo_api.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(UUID id) {
        super("Could not find  " + id);
    }
}
