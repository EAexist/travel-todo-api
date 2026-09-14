/* https://github.com/spring-attic/aws-refapp/blob/main/src/main/java/org/springframework/cloud/aws/sample/sqs/CreateReservation.java */
package com.matchalab.travel_todo_api.reservation_analysis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CreateReservation (
        @JsonProperty("job_id") UUID jobId,
        @JsonProperty("e2e_operation_start_time") Instant e2eOperationStartTime,
        @JsonProperty("message") String message,
        @JsonProperty("priority") int priority
) {
    public CreateReservation(UUID jobId, Instant e2eOperationStartTime) {
        this(jobId, e2eOperationStartTime, "", 0);
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "CreateReservation{" +
                "jobId=" + jobId +
                ", e2eOperationStartTime=" + e2eOperationStartTime +
                ", message='" + message + '\'' +
                ", priority=" + priority +
                '}';
    }
}
