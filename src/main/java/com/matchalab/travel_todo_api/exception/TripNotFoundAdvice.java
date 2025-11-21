package com.matchalab.travel_todo_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TripNotFoundAdvice {

    @ExceptionHandler(TripNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tripNotFoundHandler(TripNotFoundException ex) {
        return ex.getMessage();
    }
}
