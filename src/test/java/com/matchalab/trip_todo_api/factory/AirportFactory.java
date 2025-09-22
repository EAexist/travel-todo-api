package com.matchalab.trip_todo_api.factory;

import com.matchalab.trip_todo_api.model.Flight.Airport;

public class AirportFactory {
    public static Airport createValidAirport(String IATA) {
        switch (IATA) {
            case "TKS":
                return (new Airport("TKS", "도쿠시마 공항", "도쿠시마", "JP"));
            case "KIX":
                return (new Airport("KIX", "간사이 국제공항", "오사카", "JP"));
            case "ICN":
                return (new Airport("ICN", "인천국제공항", "서울", "KR"));
            default:
                return (new Airport(IATA));
        }
    }
}
