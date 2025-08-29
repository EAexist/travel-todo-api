package com.matchalab.trip_todo_api.factory;

import com.matchalab.trip_todo_api.model.Destination;

public class DestinationFactory {
    public static Destination createValidDestination(String title) {
        switch (title) {
            case "오사카":
                return new Destination("오사카", "JP", "간사이", "");

            case "교토":
                return new Destination("교토", "JP", "간사이", "");

            case "도쿠시마":
                return new Destination("도쿠시마", "JP", "시코쿠", "");

            default:
                return new Destination();
        }
    }
}
