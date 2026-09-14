package processor

import (
	"context"
	"reservation-worker/internal/create_reservation_job"
	"reservation-worker/internal/reservation"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
	"github.com/google/uuid"
	"github.com/stretchr/testify/mock"
	"gorm.io/gorm"
)

// MockSQSClient is a mock of SQSInterface
type MockSQSClient struct {
	mock.Mock
}

func (m *MockSQSClient) ReceiveMessage(ctx context.Context, input *sqs.ReceiveMessageInput) ([]types.Message, error) {
	args := m.Called(ctx, input)
	return args.Get(0).([]types.Message), args.Error(1)
}

func (m *MockSQSClient) DeleteMessage(ctx context.Context, queueURL string, receiptHandle *string) error {
	args := m.Called(ctx, queueURL, receiptHandle)
	return args.Error(0)
}

// MockCreateReservationJobRepository is a mock of CreateReservationJobRepository
type MockCreateReservationJobRepository struct {
	mock.Mock
}

func (m *MockCreateReservationJobRepository) Find(ctx context.Context, id uuid.UUID) (*create_reservation_job.CreateReservationJob, error) {
	args := m.Called(ctx, id)
	if args.Get(0) == nil {
		return nil, args.Error(1)
	}
	return args.Get(0).(*create_reservation_job.CreateReservationJob), args.Error(1)
}

// MockReservationRepository is a mock of ReservationRepositoryInterface
type MockReservationRepository struct {
	mock.Mock
}

func (m *MockReservationRepository) Save(ctx context.Context, reservations []reservation.Reservation) error {
	args := m.Called(ctx, reservations)
	return args.Error(0)
}

// MockAIClient is a mock of AIClientInterface
type MockAIClient struct {
	mock.Mock
}

func (m *MockAIClient) GenerateContent(ctx context.Context, prompt string) (string, error) {
	args := m.Called(ctx, prompt)
	return args.String(0), args.Error(1)
}

type MockMetrics struct {
	mock.Mock
}

func (m *MockMetrics) ObserveReservationAnalysisE2E(
	duration time.Duration,
) {
}

func (m *MockMetrics) ObserveReservationAnalysisCompleted() {
}

func TestProcessNextMessage_Success(t *testing.T) {
	mockSQS := new(MockSQSClient)
	mockJobRepository := new(MockCreateReservationJobRepository)
	mockResRepository := new(MockReservationRepository)
	mockAI := new(MockAIClient)
	mockMetrics := new(MockMetrics)

	queueURL := "test-queue"
	p := NewProcessor(mockSQS, queueURL, 1, mockJobRepository, mockResRepository, mockAI, mockMetrics)

	jobID := uuid.MustParse("550e8400-e29b-41d4-a716-446655440000")
	msgBody := `{"job_id": "550e8400-e29b-41d4-a716-446655440000"}`
	handle := "handle-1"
	messages := []types.Message{
		{Body: &msgBody, ReceiptHandle: &handle},
	}

	ctx := context.Background()

	// Expectations
	mockSQS.On("DeleteMessage", ctx, queueURL, &handle).Return(nil)
	mockJobRepository.On("Find", ctx, jobID).Return(&create_reservation_job.CreateReservationJob{ID: jobID}, nil)
	mockAI.On("GenerateContent", ctx, mock.Anything).Return("{}", nil)
	mockResRepository.On("Save", ctx, mock.Anything).Return(nil)

	// Execute
	p.processMessage(ctx, messages[0])

	// Verify
	mockSQS.AssertExpectations(t)
	mockJobRepository.AssertExpectations(t)
	mockResRepository.AssertExpectations(t)
	mockAI.AssertExpectations(t)
}

func TestProcessNextMessage_DBError(t *testing.T) {
	mockSQS := new(MockSQSClient)
	mockJobRepository := new(MockCreateReservationJobRepository)
	mockResRepository := new(MockReservationRepository)
	mockAI := new(MockAIClient)
	mockMetrics := new(MockMetrics)

	queueURL := "test-queue"
	p := NewProcessor(mockSQS, queueURL, 1, mockJobRepository, mockResRepository, mockAI, mockMetrics)

	jobID := uuid.MustParse("550e8400-e29b-41d4-a716-446655440000")
	msgBody := `{"job_id": "550e8400-e29b-41d4-a716-446655440000"}`
	handle := "handle-1"
	messages := []types.Message{
		{Body: &msgBody, ReceiptHandle: &handle},
	}

	ctx := context.Background()

	// Expectations
	mockJobRepository.On("Find", ctx, jobID).Return(nil, gorm.ErrRecordNotFound)
	// Should NOT call DeleteMessage

	// Execute
	p.processMessage(ctx, messages[0])

	// Verify
	mockSQS.AssertExpectations(t)
	mockJobRepository.AssertExpectations(t)
	mockResRepository.AssertExpectations(t)
	mockAI.AssertExpectations(t)
}
