package com.matchalab.trip_todo_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.matchalab.trip_todo_api.service.ChatModelService.GeminiChatModelService;

/*
https://github.com/spring-attic/spring-cloud-gcp/blob/main/spring-cloud-gcp-vision/src/test/java/org/springframework/cloud/gcp/vision/CloudVisionTemplateTests.java
*/
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({ "local", "local-init-data" })
// @EnabledIfSystemProperty(named = "it.vision", matches = "true")
public class GeminiChatModelServiceTest {

    @Autowired
    private GeminiChatModelService geminiChatModelService;

    // @Test
    // void extractReservationFromText_test() {
    // geminiChatModelService.extractReservationFromText(null);
    // }
}
