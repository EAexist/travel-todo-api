package config

import (
	"fmt"
	"log"
	"os"
	"strconv"

	"github.com/joho/godotenv"
)

type Config struct {
	DatabaseURL        string
	GenAiClientBaseURL *string
	GenAiAPIKey        string
	GenAimodel         string
	SQSEndpointURL     string
	SQSQueueURL        string
	MaxWorkers         int
}

// LoadConfig loads environment variables from a .env file and returns a Config struct.
func LoadConfig() (*Config, error) {
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, relying on system environment variables")
	}

	maxWorkers, err := strconv.Atoi(os.Getenv("WORKER_MAX_WORKERS"))
	if err != nil {
		maxWorkers = 60 // Default
	}

	apiKey := os.Getenv("GENAI_API_KEY")
	if apiKey == "" {
		return nil, fmt.Errorf("GENAI_API_KEY is not set")
	}

	GenAiClientBaseURLValue := os.Getenv("GENAI_CLIENT_BASE_URL")

	var GenAiClientBaseURL *string
	if GenAiClientBaseURLValue != "" {
		GenAiClientBaseURL = &GenAiClientBaseURLValue
	}

	return &Config{
		DatabaseURL:        os.Getenv("DATABASE_URL"),
		GenAiClientBaseURL: GenAiClientBaseURL,
		GenAiAPIKey:        os.Getenv("GENAI_API_KEY"),
		GenAimodel:         os.Getenv("GENAI_MODEL"),
		SQSEndpointURL:     os.Getenv("SQS_ENDPOINT_URL"),
		SQSQueueURL:        os.Getenv("SQS_QUEUE_URL"),
		MaxWorkers:         maxWorkers,
	}, nil
}
