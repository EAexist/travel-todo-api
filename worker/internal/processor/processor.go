package processor

import (
	"context"
	"encoding/json"
	"errors"
	"log"
	"sync"
	"time"

	"reservation-worker/internal/ai"
	"reservation-worker/internal/create_reservation_job"
	"reservation-worker/internal/reservation"

	internalSQS "reservation-worker/internal/sqs"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
	"gorm.io/gorm"
)

// SQSInterface defines the methods we use from the SQS client
type SQSInterface interface {
	ReceiveMessage(ctx context.Context, input *sqs.ReceiveMessageInput) ([]types.Message, error)
	DeleteMessage(ctx context.Context, queueURL string, receiptHandle *string) error
}

// AIClientInterface defines the methods we use from the AI client
type AIClientInterface interface {
	GenerateContent(ctx context.Context, prompt string) (string, error)
}

type MetricsInterface interface {
	ObserveReservationAnalysisE2E(
		duration time.Duration,
	)
	ObserveReservationAnalysisCompleted()
}

// Processor struct updated to include aiClient
type Processor struct {
	sqsClient                      SQSInterface
	queueURL                       string
	maxWorkers                     int
	createReservationJobRepository create_reservation_job.CreateReservationJobRepositoryInterface
	ReservationRepository          reservation.ReservationRepositoryInterface
	aiClient                       AIClientInterface
	metrics                        MetricsInterface
	fetchersCnt                    int
}

func NewProcessor(sqsClient SQSInterface, queueURL string, maxWorkers int, createReservationJobRepository create_reservation_job.CreateReservationJobRepositoryInterface, ReservationRepository reservation.ReservationRepositoryInterface, aiClient AIClientInterface, metrics MetricsInterface) *Processor {
	return &Processor{
		sqsClient:                      sqsClient,
		queueURL:                       queueURL,
		maxWorkers:                     maxWorkers,
		createReservationJobRepository: createReservationJobRepository,
		ReservationRepository:          ReservationRepository,
		aiClient:                       aiClient,
		metrics:                        metrics,
		fetchersCnt:                    12,
	}
}

func (p *Processor) Run(ctx context.Context) {
	// 1. Expand buffer size to prevent fetchers from blocking during processing spikes
	bufferSize := p.maxWorkers * 50
	if bufferSize < 500 {
		bufferSize = 500
	}
	msgChan := make(chan types.Message, bufferSize)

	var fetcherWg sync.WaitGroup
	var workerWg sync.WaitGroup

	// 2. Launch worker pool
	for i := 0; i < p.maxWorkers; i++ {
		workerWg.Add(1)
		go func() {
			defer workerWg.Done()
			for {
				select {
				case <-ctx.Done():
					return
				case msg, ok := <-msgChan:
					if !ok {
						return
					}
					p.processMessage(ctx, msg)
				}
			}
		}()
	}

	// 3. Launch concurrent SQS fetchers
	for i := 0; i < p.fetchersCnt; i++ {
		fetcherWg.Add(1)
		go func() {
			defer fetcherWg.Done()
			for {
				select {
				case <-ctx.Done():
					return
				default:
					// Directly receives []types.Message
					msgs, err := p.sqsClient.ReceiveMessage(ctx, &sqs.ReceiveMessageInput{
						QueueUrl:            aws.String(p.queueURL),
						MaxNumberOfMessages: 10,
						WaitTimeSeconds:     20,
					})
					if err != nil || len(msgs) == 0 {
						continue
					}

					// Iterate directly over msgs slice
					for _, msg := range msgs {
						select {
						case msgChan <- msg:
						case <-ctx.Done():
							return
						}
					}
				}
			}
		}()
	}

	// 4. Graceful shutdown sequence
	fetcherWg.Wait()
	close(msgChan)
	workerWg.Wait()
}

func (p *Processor) processMessage(ctx context.Context, msg types.Message) {
	var sqsMsg internalSQS.SQSMessage
	if err := json.Unmarshal([]byte(*msg.Body), &sqsMsg); err != nil {
		log.Printf("Error unmarshalling message: %v", err)
		return
	}
	job, err := p.createReservationJobRepository.Find(ctx, sqsMsg.JobID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) { // or gorm.ErrRecordNotFound
			log.Printf("Job %s not found, skipping message", sqsMsg.JobID)
			return
		}
		log.Printf("Database error finding job %s: %v", sqsMsg.JobID, err)
		return
	}

	log.Printf("Processing job: %s (Priority: %d, Message: %s)", job.ID, sqsMsg.Priority, sqsMsg.Message)

	// Call Gemini AI
	prompt := ai.GenerateReservationPrompt(job.Payload.Data().ConfirmationText)
	geminiResp, err := p.aiClient.GenerateContent(ctx, prompt)
	if err != nil {
		log.Printf("Error calling Gemini AI for job %s: %v", job.ID, err)
		return
	}
	// log.Printf("Gemini AI response for job %s: %s", job.ID, geminiResp)

	// Parse and map response
	reservations, err := MapAIResponseToReservations(geminiResp)
	if err != nil {
		log.Printf("Error mapping AI response for job %s: %v", job.ID, err)
		return
	}

	for i := range reservations {
		reservations[i].TripID = job.TripID
	}

	// log.Printf("Mapped Reservations: %v", reservations)
	// Persist results
	if err := p.ReservationRepository.Save(ctx, reservations); err != nil {
		log.Printf("Error saving reservations for job %s: %v", job.ID, err)
		return
	}

	// Record metrics
	duration := time.Since(sqsMsg.E2EOperationStartTime)
	p.metrics.ObserveReservationAnalysisE2E(duration)
	p.metrics.ObserveReservationAnalysisCompleted()

	if err := p.sqsClient.DeleteMessage(ctx, p.queueURL, msg.ReceiptHandle); err != nil {
		log.Printf("Error deleting message: %v", err)
	}
}
