package com.matchalab.trip_todo_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.matchalab.trip_todo_api.service.VisionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableAsync
public class TripTodoApiApplication {

    // @Autowired
    // private final VisionService visionService;

    public static void main(String[] args) {
        SpringApplication.run(TripTodoApiApplication.class, args);
    }
}
