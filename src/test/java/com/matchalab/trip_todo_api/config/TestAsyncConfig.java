package com.matchalab.trip_todo_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class TestAsyncConfig {

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}