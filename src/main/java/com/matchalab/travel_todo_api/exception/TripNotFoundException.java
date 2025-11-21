package com.matchalab.travel_todo_api.exception;

import java.util.UUID;

public class TripNotFoundException extends RuntimeException {

    public TripNotFoundException(UUID id) {
        super("Could not find Trip " + id);
    }
}
