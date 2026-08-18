package com.matchalab.travel_todo_api.config;

import com.matchalab.travel_todo_api.service.ReservationCategoryChatResultService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FunctionConfig {

  @Bean
  ReservationCategoryChatResultService getReservationCategory() {
    return new ReservationCategoryChatResultService();
  }
}
