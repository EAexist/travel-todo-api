package com.matchalab.travel_todo_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SchemaGeneratorConfig {

  @Bean
  @Profile("schema-generation")
  public CommandLineRunner terminateAfterSchema(ApplicationContext ctx) {
    return args -> {
      System.out.println("✅ Schema generation complete. Terminating...");
      System.exit(0);
    };
  }
}
