package com.matchalab.travel_todo_api.event;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewTripCreatedEvent extends ApplicationEvent {
    private final UUID tripId;

    public NewTripCreatedEvent(Object source, UUID tripId) {
        super(source);
        this.tripId = tripId;
    }
}