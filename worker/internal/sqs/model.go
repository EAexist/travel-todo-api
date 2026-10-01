package sqs

import (
	"time"

	"github.com/google/uuid"
)

type SQSMessage struct {
	JobID                 uuid.UUID `json:"job_id"`
	E2EOperationStartTime time.Time `json:"e2e_operation_start_time"`
	Message               string    `json:"message"`
	Priority              int       `json:"priority"`
}
