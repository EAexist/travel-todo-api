package com.matchalab.trip_todo_api.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewTripCreatedEvent extends ApplicationEvent {
    private final Long tripId;

    public NewTripCreatedEvent(Object source, Long tripId) {
        super(source);
        this.tripId = tripId;
    }
}