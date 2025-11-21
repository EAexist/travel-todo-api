package com.matchalab.travel_todo_api.event;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewFlightRouteCreatedEvent extends ApplicationEvent {
    private final UUID flightRouteId;

    public NewFlightRouteCreatedEvent(Object source, UUID flightRouteId) {
        super(source);
        this.flightRouteId = flightRouteId;
    }
}