package com.matchalab.travel_todo_api.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsAsyncOperations;
import io.awspring.cloud.sqs.operations.SqsOperations;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsConfig {

//    @Bean
//    public SqsAsyncOperations sqsAsyncOperations(SqsAsyncClient sqsAsyncClient) {
//        return SqsTemplate.newAsyncTemplate(sqsAsyncClient);
//    }
//    @Bean
//    public SqsOperations sqsOperations(SqsAsyncClient sqsAsyncClient) {
//        return SqsTemplate.newSyncTemplate(sqsAsyncClient);
//    }

    @Bean
    public SqsMessagingMessageConverter sqsMessageConverter(
            ObjectMapper objectMapper
    ) {
        SqsMessagingMessageConverter converter =
                new SqsMessagingMessageConverter();

        converter.setObjectMapper(objectMapper);

        return converter;
    }
    @Bean
    public SqsAsyncOperations sqsAsyncOperations(
            SqsAsyncClient sqsAsyncClient,
            SqsMessagingMessageConverter converter
    ) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .messageConverter(converter)
                .build();
    }

    @Bean
    public SqsOperations sqsOperations(
            SqsAsyncClient sqsAsyncClient,
            SqsMessagingMessageConverter converter
    ) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .messageConverter(converter)
                .buildSyncTemplate();
    }
}