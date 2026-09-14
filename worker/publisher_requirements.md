@Builder
public record CreateReservation (
        @JsonProperty("job_id") UUID jobId,
        @JsonProperty("e2e_operation_start_time") Instant e2eOperationStartTime,
        @JsonProperty("message") String message,
        @JsonProperty("priority") int priority
) {
    @JsonCreator
    public CreateReservation(UUID jobId, Instant e2eOperationStartTime) {
        this(jobId, e2eOperationStartTime, "", 0);
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "CreateReservation{" + "message='" + message + '\'' + ", priority=" + priority + '}';
    }
}

CreateReservation message = new CreateReservation(jobId, e2eOperationStartTime);

try {
    SendResult<CreateReservation> sendResult = this.sqsTemplate.send(appSqsProperties.getReservationAnalysisQueueName(), message);
}