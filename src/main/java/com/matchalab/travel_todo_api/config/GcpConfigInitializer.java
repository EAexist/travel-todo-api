package com.matchalab.travel_todo_api.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

public class GcpConfigInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext context) {
    String path = "/tmp/gcp-wif-config.json";
    File file = new File(path);

    if (!file.exists()) {
      try (SsmClient ssmClient = SsmClient.create()) {
        String configJson =
            ssmClient
                .getParameter(
                    GetParameterRequest.builder()
                        .name("/stg/travel-todo-api/GCP_WIF_CONFIG")
                        .build())
                .parameter()
                .value();
        Files.write(Paths.get(path), configJson.getBytes());
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", path);
        System.setProperty("spring.cloud.gcp.credentials.location", "file:" + path);
      } catch (Exception e) {
        // Log the error appropriately for Lambda environment
        System.err.println("Failed to initialize GCP WIF Config: " + e.getMessage());
      }
    }
  }
}
