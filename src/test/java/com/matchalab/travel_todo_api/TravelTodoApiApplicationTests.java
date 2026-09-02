package com.matchalab.travel_todo_api;

import com.matchalab.travel_todo_api.config.PostgresTestContainerSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TravelTodoApiApplicationTests implements PostgresTestContainerSupport {

  @Test
  void contextLoads() {}
}
