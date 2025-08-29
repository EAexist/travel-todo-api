package com.matchalab.trip_todo_api.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NewFlightRouteCreatedEvent extends ApplicationEvent {
    private final Long flightRouteId;

    public NewFlightRouteCreatedEvent(Object source, Long flightRouteId) {
        super(source);
        this.flightRouteId = flightRouteId;
    }
}