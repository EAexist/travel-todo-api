package com.matchalab.trip_todo_api.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewDestinationCreatedEvent extends ApplicationEvent {
    private final Long destinationId;

    public NewDestinationCreatedEvent(Object source, Long destinationId) {
        super(source);
        this.destinationId = destinationId;
    }
}