package com.matchalab.travel_todo_api.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    // @Bean
    // ChatClient chatClient() {
    // return ChatClient.builder(null).build();
    // }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}