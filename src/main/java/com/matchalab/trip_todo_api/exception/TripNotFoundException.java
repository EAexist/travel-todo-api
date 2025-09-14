package com.matchalab.trip_todo_api.exception;

public class TripNotFoundException extends RuntimeException {

    public TripNotFoundException(String id) {
        super("Could not find Trip " + id);
    }
}
