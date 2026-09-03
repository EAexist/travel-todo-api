package com.matchalab.travel_todo_api.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
class AiBillingSafetyGuard {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @PostConstruct
    void validateNonLiveBaseUrl() {
        boolean isRealApi = baseUrl.contains("googleapis.com") || baseUrl.contains("openai.com");
        if (isRealApi) {
            throw new IllegalStateException(
                    "CRITICAL ERROR: 'load-test' profile is active, but base-url points to a REAL AI API ($baseUrl)!"
            );
        }
    }
}