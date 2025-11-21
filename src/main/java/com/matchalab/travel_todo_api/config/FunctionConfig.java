package com.matchalab.travel_todo_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.matchalab.travel_todo_api.service.ReservationCategoryChatResultService;

@Configuration
public class FunctionConfig {

    @Bean
    ReservationCategoryChatResultService getReservationCategory() {
        return new ReservationCategoryChatResultService();
    }
}