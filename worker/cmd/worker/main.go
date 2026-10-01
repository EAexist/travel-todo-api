package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	"reservation-worker/internal/ai"
	"reservation-worker/internal/config"
	"reservation-worker/internal/create_reservation_job"
	"reservation-worker/internal/db"
	"reservation-worker/internal/metrics"
	"reservation-worker/internal/processor"
	"reservation-worker/internal/reservation"
	"reservation-worker/internal/sqs"
)

func main() {
	// Initialize context for cancellation with signal handling
	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	// Load Config
	cfg, err := config.LoadConfig()
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	// Initialize DB connection
	database, err := db.NewDBConnection()
	if err != nil {
		log.Fatalf("Failed to connect to database: %v", err)
	}

	// Initialize SQS Client
	sqsClient, err := sqs.NewSQSClient(ctx)
	if err != nil {
		log.Fatalf("Failed to init SQS: %v", err)
	}

	// Initialize Repositorys and AI Client
	Repository := db.NewRepository(database)
	jobRepository := create_reservation_job.NewCreateReservationJobRepository(Repository)
	resRepository := reservation.NewReservationRepository(Repository)
	aiClient, err := ai.NewClient(ctx, *cfg)
	if err != nil {
		log.Fatalf("Failed to init AI client: %v", err)
	}
	defer aiClient.Close()

	// Initialize Processor
	m := metrics.New()
	go func() {
		if err := m.StartServer(":2112"); err != nil && err != http.ErrServerClosed {
			log.Fatalf("Metrics server failed: %v", err)
		}
	}()

	proc := processor.NewProcessor(sqsClient, cfg.SQSQueueURL, cfg.MaxWorkers, jobRepository, resRepository, aiClient, m)

	log.Println("Worker started, polling for messages...")

	// Start Processor Loop (blocks until context is cancelled)
	proc.Run(ctx)

	log.Println("Worker shut down gracefully.")
}
