package com.matchalab.trip_todo_api.service;

import java.util.function.Function;

import com.matchalab.trip_todo_api.enums.ReservationCategoryChatResult;

public class ReservationCategoryChatResultService implements Function<String, ReservationCategoryChatResult> {

    @Override
    public ReservationCategoryChatResult apply(String text) {
        return ReservationCategoryChatResult.OTHER_RESERVATION;
    }
}