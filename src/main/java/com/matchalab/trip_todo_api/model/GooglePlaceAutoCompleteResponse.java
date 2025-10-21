package com.matchalab.trip_todo_api.model;

import java.util.List;

import lombok.Builder;

@Builder
public record GooglePlaceAutoCompleteResponse(
        List<GooglePlaceData> predictions,
        String status) {
}