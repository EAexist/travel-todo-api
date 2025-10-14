package com.matchalab.trip_todo_api.factory;

import com.matchalab.trip_todo_api.model.Flight.Airport;

public class AirportFactory {
    public static Airport createValidAirport(String IATA) {
        switch (IATA) {
            case "TKS":
                return (Airport.builder().iataCode("TKS").airportName("도쿠시마 공항").cityName("도쿠시마")
                        .iso2DigitNationCode("JP").build());
            case "KIX":
                return (Airport.builder().iataCode("KIX").airportName("간사이 국제공항").cityName("오사카")
                        .iso2DigitNationCode("JP").build());
            case "ICN":
                return (Airport.builder().iataCode("ICN").airportName("인천국제공항").cityName("서울").iso2DigitNationCode("KR")
                        .build());
            default:
                return (Airport.builder().iataCode(IATA).build());
        }
    }
}
