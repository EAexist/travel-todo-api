package com.matchalab.trip_todo_api.factory;

import com.matchalab.trip_todo_api.model.Flight.Airport;

public class AirportFactory {
    public static Airport createValidAirport(String IATA) {
        return (new Airport(IATA));
    }
}
