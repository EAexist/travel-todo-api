package com.matchalab.trip_todo_api.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewFlightRouteCreatedEvent extends ApplicationEvent {
    private final String flightRouteId;

    public NewFlightRouteCreatedEvent(Object source, String flightRouteId) {
        super(source);
        this.flightRouteId = flightRouteId;
    }
}