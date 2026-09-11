package com.matchalab.travel_todo_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.travel_todo_api.config.GcpConfigInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableAsync
@EnableCaching
@ConfigurationPropertiesScan
public class TravelTodoApiApplication {

  // @Autowired
  // private final VisionService visionService;

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(TravelTodoApiApplication.class);
    app.addInitializers(new GcpConfigInitializer());
    app.run(args);
  }

  @Bean
  CommandLineRunner checkJackson(ObjectMapper objectMapper) {
    return args -> {
      System.out.println(
              String.format("Modules: %s",
                      objectMapper.getRegisteredModuleIds())
      );
    };
  }

}
