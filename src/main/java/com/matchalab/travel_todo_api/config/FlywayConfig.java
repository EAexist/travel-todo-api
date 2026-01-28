package com.matchalab.travel_todo_api.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

//@Configuration
//public class FlywayConfig {
//
//    @Bean
//    @Profile("dev")
//    public FlywayMigrationStrategy cleanMigrateStrategy() {
//        System.out.println("✅ [cleanMigrateStrategy]");
//        return flyway -> {
//            flyway.clean();
//            flyway.migrate();
//        };
//    }
//}