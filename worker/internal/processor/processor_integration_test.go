package processor

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"sync"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/localstack"
	"github.com/testcontainers/testcontainers-go/modules/postgres"
	"gorm.io/datatypes"

	"reservation-worker/internal/create_reservation_job"
	"reservation-worker/internal/db"
	"reservation-worker/internal/reservation"

	internalSQS "reservation-worker/internal/sqs"
)

// IntegrationMockAIClient for testing
type IntegrationMockAIClient struct{}

func (m *IntegrationMockAIClient) GenerateContent(ctx context.Context, prompt string) (string, error) {
	return `{
		"flightTickets": [
			{
				"flightNumber": "JL123",
				"departureAirportIataCode": "HND",
				"arrivalAirportIataCode": "JFK"
			},
			{
				"flightNumber": "JL456",
				"departureAirportIataCode": "JFK",
				"arrivalAirportIataCode": "HND"
			}
		]
	}`, nil
}

// SQSWrapper satisfies SQSInterface
type SQSWrapper struct {
	*sqs.Client
}

func (s *SQSWrapper) ReceiveMessage(ctx context.Context, input *sqs.ReceiveMessageInput) ([]types.Message, error) {
	out, err := s.Client.ReceiveMessage(ctx, input)
	if err != nil {
		log.Printf("ReceiveMessage error: %v", err)
		return nil, err
	}
	log.Printf("Received %d messages", len(out.Messages))
	return out.Messages, nil
}

func (s *SQSWrapper) DeleteMessage(ctx context.Context, queueURL string, receiptHandle *string) error {
	_, err := s.Client.DeleteMessage(ctx, &sqs.DeleteMessageInput{
		QueueUrl:      &queueURL,
		ReceiptHandle: receiptHandle,
	})
	if err != nil {
		log.Printf("DeleteMessage error: %v", err)
	}
	return err
}

func TestProcessor_Integration(t *testing.T) {
	t.Setenv("TESTCONTAINERS_RYUK_DISABLED", "true")
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// 1. Setup Postgres
	pgContainer, err := postgres.Run(ctx, "postgres:17.6",
		postgres.WithDatabase("db"),
		postgres.WithUsername("postgres"),
		postgres.WithPassword("password"),
		postgres.BasicWaitStrategies(),
		testcontainers.WithReuseByName("testdb"),
	)
	require.NoError(t, err)
	defer pgContainer.Terminate(ctx)
	pgURL, err := pgContainer.ConnectionString(ctx, "sslmode=disable")
	require.NoError(t, err)
	os.Setenv("DATABASE_URL", pgURL)

	// 2. Setup LocalStack
	lsContainer, err := localstack.Run(ctx, "localstack/localstack:4.12.0", testcontainers.WithReuseByName("localstack"))
	require.NoError(t, err)

	lsEndpoint, err := lsContainer.Endpoint(ctx, "")
	require.NoError(t, err)
	baseEndpoint := fmt.Sprintf("http://%s", lsEndpoint)

	// 3. Create SQS Queue
	cfg, err := config.LoadDefaultConfig(ctx,
		config.WithRegion("ap-northeast-2"),
		config.WithCredentialsProvider(
			credentials.NewStaticCredentialsProvider("TEST_LOCALSTACK_ACCESS_KEY", "TEST_LOCALSTACK_SECRET_KEY", "TEST_LOCALSTACK_SESSION"),
		),
	)
	require.NoError(t, err)
	sqsClient := sqs.NewFromConfig(cfg, func(o *sqs.Options) {
		o.BaseEndpoint = &baseEndpoint
	})

	queueName := "test-queue"
	createOut, err := sqsClient.CreateQueue(ctx, &sqs.CreateQueueInput{QueueName: &queueName})
	require.NoError(t, err)

	// 4. Setup Database
	// We need to use the connection string set in os.Setenv("DATABASE_URL", pgURL)
	// NewDBConnection uses os.Getenv("DATABASE_URL") internally, which should pick it up.
	dbConn, err := db.NewDBConnection()
	require.NoError(t, err)
	err = dbConn.AutoMigrate(&create_reservation_job.CreateReservationJob{}, &reservation.Reservation{})
	require.NoError(t, err)

	// 5. Insert Job
	jobID := uuid.New()
	job := create_reservation_job.CreateReservationJob{
		ID:        jobID,
		Status:    "pending",
		CreatedAt: time.Now(),
		TripID:    uuid.New(),
		Payload: datatypes.NewJSONType(create_reservation_job.CreateReservationPayload{
			ConfirmationText: "Please book a flight to Tokyo",
		}),
	}
	err = dbConn.Create(&job).Error
	require.NoError(t, err)

	// 6. Initialize Processor
	repository := db.NewRepository(dbConn)
	jobRepository := create_reservation_job.NewCreateReservationJobRepository(repository)
	resRepository := reservation.NewReservationRepository(repository)
	mockAI := &IntegrationMockAIClient{}
	mockMetrics := new(MockMetrics)

	proc := NewProcessor(&SQSWrapper{sqsClient}, *createOut.QueueUrl, 1, jobRepository, resRepository, mockAI, mockMetrics)

	// 7. Start Processor
	go proc.Run(ctx)
	time.Sleep(1 * time.Second)

	// 8. Send Message
	msgBody, _ := json.Marshal(internalSQS.SQSMessage{
		JobID:                 jobID,
		E2EOperationStartTime: time.Now(),
	})
	msgBodyStr := string(msgBody)
	_, err = sqsClient.SendMessage(ctx, &sqs.SendMessageInput{
		QueueUrl:    createOut.QueueUrl,
		MessageBody: &msgBodyStr,
	})
	require.NoError(t, err)

	// 9. Verify
	require.Eventually(t, func() bool {
		var reservations []reservation.Reservation
		err = dbConn.Find(&reservations).Error
		if err != nil {
			log.Printf("Failed to list reservations: %v", err)
			return false
		}

		count := 0
		for _, r := range reservations {
			log.Printf("Checking reservation: ID=%v, Category=%s", r.ID, r.Category)
			if r.Category == reservation.FLIGHT_TICKET {
				count++
			}
		}

		return count == 2
	}, 10*time.Second, 500*time.Millisecond)
}

func TestProcessor_StreamingThroughput(t *testing.T) {
	t.Setenv("TESTCONTAINERS_RYUK_DISABLED", "true")
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// 1. Setup Postgres & LocalStack (reusing existing setup)
	pgContainer, err := postgres.Run(ctx, "postgres:17.6",
		postgres.WithDatabase("db"),
		postgres.WithUsername("postgres"),
		postgres.WithPassword("password"),
		postgres.BasicWaitStrategies(),
		testcontainers.WithReuseByName("testdb"),
	)
	require.NoError(t, err)
	pgURL, err := pgContainer.ConnectionString(ctx, "sslmode=disable")
	require.NoError(t, err)
	t.Setenv("DATABASE_URL", pgURL)

	lsContainer, err := localstack.Run(ctx, "localstack/localstack:4.12.0", testcontainers.WithReuseByName("localstack"))
	require.NoError(t, err)

	lsEndpoint, err := lsContainer.Endpoint(ctx, "")
	require.NoError(t, err)
	baseEndpoint := fmt.Sprintf("http://%s", lsEndpoint)

	httpClient := &http.Client{
		Transport: &http.Transport{
			MaxIdleConns:        200,
			MaxIdleConnsPerHost: 100,
			MaxConnsPerHost:     100,
		},
	}
	cfg, err := config.LoadDefaultConfig(ctx,
		config.WithRegion("ap-northeast-2"),
		config.WithHTTPClient(httpClient),
		config.WithCredentialsProvider(
			credentials.NewStaticCredentialsProvider("TEST_LOCALSTACK_ACCESS_KEY", "TEST_LOCALSTACK_SECRET_KEY", "TEST_LOCALSTACK_SESSION"),
		),
	)
	require.NoError(t, err)

	sqsClient := sqs.NewFromConfig(cfg, func(o *sqs.Options) {
		o.BaseEndpoint = &baseEndpoint
	})

	queueName := fmt.Sprintf("stream-queue-%d", time.Now().UnixNano())
	createOut, err := sqsClient.CreateQueue(ctx, &sqs.CreateQueueInput{QueueName: &queueName})
	require.NoError(t, err)

	dbConn, err := db.NewDBConnection()
	require.NoError(t, err)
	err = dbConn.AutoMigrate(&create_reservation_job.CreateReservationJob{}, &reservation.Reservation{})
	require.NoError(t, err)

	// 2. Initialize Processor FIRST (Queue starts empty)
	repository := db.NewRepository(dbConn)
	jobRepository := create_reservation_job.NewCreateReservationJobRepository(repository)
	resRepository := reservation.NewReservationRepository(repository)
	mockAI := &IntegrationMockAIClient{}
	mockMetrics := new(MockMetrics)

	proc := NewProcessor(
		&SQSWrapper{sqsClient},
		*createOut.QueueUrl,
		60, // maxWorkers
		jobRepository,
		resRepository,
		mockAI,
		mockMetrics,
	)

	// Start processor listening on empty queue
	go proc.Run(ctx)

	// 3. Start Streaming Producer (~100 RPS target)
	const totalMessages = 500
	const targetRPS = 100
	ticker := time.NewTicker(time.Second / time.Duration(targetRPS)) // ~10ms interval
	defer ticker.Stop()

	var producerWg sync.WaitGroup
	producerWg.Add(1)

	startTime := time.Now()

	go func() {
		defer producerWg.Done()
		batch := make([]types.SendMessageBatchRequestEntry, 0, 10)

		for i := 0; i < totalMessages; i++ {
			<-ticker.C

			jobID := uuid.New()
			job := create_reservation_job.CreateReservationJob{
				ID:        jobID,
				Status:    "pending",
				CreatedAt: time.Now(),
				TripID:    uuid.New(),
				Payload: datatypes.NewJSONType(create_reservation_job.CreateReservationPayload{
					ConfirmationText: "Please book a flight to Tokyo",
				}),
			}

			_ = dbConn.Create(&job).Error

			msgBody, _ := json.Marshal(internalSQS.SQSMessage{
				JobID:                 jobID,
				E2EOperationStartTime: time.Now(),
			})
			msgStr := string(msgBody)
			entryID := fmt.Sprintf("msg-%d", i)

			batch = append(batch, types.SendMessageBatchRequestEntry{
				Id:          &entryID,
				MessageBody: &msgStr,
			})

			// Flush batch every 10 messages or at final message
			if len(batch) == 10 || i == totalMessages-1 {
				_, _ = sqsClient.SendMessageBatch(ctx, &sqs.SendMessageBatchInput{
					QueueUrl: createOut.QueueUrl,
					Entries:  batch,
				})
				batch = batch[:0]
			}
		}
	}()

	producerWg.Wait()
	t.Logf("Finished publishing %d messages in %v", totalMessages, time.Since(startTime))

	// 4. Verify Processor Keeps Up With Stream
	expectedReservations := totalMessages * 2
	testDBConn, err := db.NewDBConnection()
	require.NoError(t, err)

	require.Eventually(t, func() bool {
		var count int64
		// Query using the isolated connection
		err := testDBConn.Model(&reservation.Reservation{}).
			Where("category = ?", reservation.FLIGHT_TICKET).
			Count(&count).Error
		if err != nil {
			return false
		}
		return count == int64(expectedReservations)
	}, 30*time.Second, 500*time.Millisecond)

	totalDuration := time.Since(startTime)
	effectiveRPS := float64(totalMessages) / totalDuration.Seconds()

	t.Logf("End-to-end stream process completed in %v (Effective RPS: %.2f)", totalDuration, effectiveRPS)

	// Ensure system handled stream without lag building up
	require.GreaterOrEqual(t, effectiveRPS, 50.0, "Streaming throughput fell significantly below publisher rate")
}
