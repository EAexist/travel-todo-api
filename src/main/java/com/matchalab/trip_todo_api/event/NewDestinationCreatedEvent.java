package com.matchalab.trip_todo_api.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewDestinationCreatedEvent extends ApplicationEvent {
    private final String destinationId;

    public NewDestinationCreatedEvent(Object source, String destinationId) {
        super(source);
        this.destinationId = destinationId;
    }
}