package com.matchalab.trip_todo_api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.matchalab.trip_todo_api.model.Airport;
import com.matchalab.trip_todo_api.model.Destination;

@TestConfiguration
public class RecommendedFlightTestConfig {

    @Bean
    String KansaiIntlIATA() {
        return "KIX";
    }

    @Bean
    String IncheonIntlIATA() {
        return "ICN";
    }

    @Bean
    Airport KansaiIntl() {
        return new Airport(KansaiIntlIATA());
    }

    @Bean
    Airport IncheonIntl() {
        return new Airport(IncheonIntlIATA());
    }

    @Bean
    Destination defaultDestination() {
        return new Destination("교토", "JP", "일본 교토부", "null");
    }

    @Bean
    Destination destinationWithRecommendedFlight() {
        return new Destination("교토", "JP", "일본 교토부", "null");
    }
}
