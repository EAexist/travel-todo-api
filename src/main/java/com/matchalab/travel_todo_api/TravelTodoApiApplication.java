package com.matchalab.travel_todo_api;

import com.matchalab.travel_todo_api.config.GcpConfigInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableAsync
@EnableCaching
public class    TravelTodoApiApplication {

    // @Autowired
    // private final VisionService visionService;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TravelTodoApiApplication.class);
        app.addInitializers(new GcpConfigInitializer());
        app.run(args);
    }
}
