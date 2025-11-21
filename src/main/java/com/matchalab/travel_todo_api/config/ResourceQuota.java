package com.matchalab.travel_todo_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.quota")
@Getter
@Setter
public class ResourceQuota {

    private int maxTrips;
    private int maxTripDurationDays;
    private int maxDestinations;
    private int maxTodos;
    private int maxReservations;
}