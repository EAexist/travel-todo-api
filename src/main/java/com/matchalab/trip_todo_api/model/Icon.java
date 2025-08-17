package com.matchalab.trip_todo_api.model;

import lombok.Builder;

@Builder
public record Icon(String name, String type) {

    public Icon(String name) {
        this(name, "tossface");
    }
}
