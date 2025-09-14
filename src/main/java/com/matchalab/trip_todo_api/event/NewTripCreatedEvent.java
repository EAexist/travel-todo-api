package com.matchalab.trip_todo_api.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewTripCreatedEvent extends ApplicationEvent {
    private final String tripId;

    public NewTripCreatedEvent(Object source, String tripId) {
        super(source);
        this.tripId = tripId;
    }
}