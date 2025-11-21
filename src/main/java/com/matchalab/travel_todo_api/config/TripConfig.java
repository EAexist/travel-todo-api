package com.matchalab.travel_todo_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.matchalab.travel_todo_api.service.VisionService;

@Configuration
// @EnableJpaRepositories(basePackages = "com.matchalab.trip_todo_api")
// @ComponentScan(basePackageClasses = TripMapper.class)
public class TripConfig {

    // @Bean
    // VisionService visionService() {
    // return new VisionService();
    // }
    // @Bean
    // TripService tripService(
    // TripRepository tripRepository,
    // TodoRepository todoRepository,
    // AccomodationRepository accomodationRepository,
    // TripMapper tripMapper) {
    // return new TripService(tripRepository,
    // todoRepository,
    // accomodationRepository,
    // tripMapper);
    // }

    // @Bean
    // public TripRepository tripRepository();
}
