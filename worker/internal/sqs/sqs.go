package sqs

import (
	"context"
	"net/http"
	"os"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
)

type SQSClient struct {
	client *sqs.Client
}

func NewSQSClient(ctx context.Context) (*SQSClient, error) {
	// Configure custom HTTP Transport to eliminate default connection pool bottlenecks (default MaxIdleConnsPerHost is 2)
	httpClient := &http.Client{
		Transport: &http.Transport{
			MaxIdleConns:        200,
			MaxIdleConnsPerHost: 100,
			MaxConnsPerHost:     100,
		},
	}

	cfg, err := config.LoadDefaultConfig(ctx,
		config.WithHTTPClient(httpClient),
	)
	if err != nil {
		return nil, err
	}

	endpoint := os.Getenv("SQS_ENDPOINT_URL")
	var client *sqs.Client
	if endpoint != "" {
		client = sqs.NewFromConfig(cfg, func(o *sqs.Options) {
			o.BaseEndpoint = &endpoint
		})
	} else {
		client = sqs.NewFromConfig(cfg)
	}

	return &SQSClient{
		client: client,
	}, nil
}

func (s *SQSClient) ReceiveMessage(ctx context.Context, input *sqs.ReceiveMessageInput) ([]types.Message, error) {
	out, err := s.client.ReceiveMessage(ctx, input)
	if err != nil {
		return nil, err
	}
	return out.Messages, nil
}

func (s *SQSClient) DeleteMessage(ctx context.Context, queueURL string, receiptHandle *string) error {
	_, err := s.client.DeleteMessage(ctx, &sqs.DeleteMessageInput{
		QueueUrl:      &queueURL,
		ReceiptHandle: receiptHandle,
	})
	return err
}
