package com.matchalab.travel_todo_api.jobs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.sqs")
public class AppSqsProperties{
    private final String reservationAnalysisQueueName;

    public AppSqsProperties(String reservationAnalysisQueueName) {
        this.reservationAnalysisQueueName = reservationAnalysisQueueName;
    }

    public String getReservationAnalysisQueueName() {
        return reservationAnalysisQueueName;
    }
}