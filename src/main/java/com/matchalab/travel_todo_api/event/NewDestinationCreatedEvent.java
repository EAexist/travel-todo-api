package com.matchalab.travel_todo_api.event;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewDestinationCreatedEvent extends ApplicationEvent {
    private final UUID destinationId;

    public NewDestinationCreatedEvent(Object source, UUID destinationId) {
        super(source);
        this.destinationId = destinationId;
    }
}