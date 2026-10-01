package main

import (
	"context"
	"sync"

	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sqs/types"
)

type Processor struct {
	sqsClient *sqs.Client
	queueURL  string
	maxWorkers int
}

func (p *Processor) Run(ctx context.Context) {
	// Semaphore channel controls active concurrent workers
	sem := make(chan struct{}, p.maxWorkers)
	var wg sync.WaitGroup

	for {
		select {
		case <-ctx.Done():
			// Stop accepting new work and wait for in-flight LLM requests to finish
			wg.Wait()
			return
		default:
			// Acquire slot before fetching from SQS to avoid holding idle messages
			select {
			case sem <- struct{}{}:
			case <-ctx.Done():
				wg.Wait()
				return
			}

			// Receive up to 10 messages (SQS max batch size)
			out, err := p.sqsClient.ReceiveMessage(ctx, &sqs.ReceiveMessageInput{
				QueueUrl:            &p.queueURL,
				MaxNumberOfMessages: 10,
				WaitTimeSeconds:     20, // Long polling
			})
			if err != nil || len(out.Messages) == 0 {
				<-sem // Release slot if fetch yielded no messages
				continue
			}

			// Launch goroutines for fetched batch
			for _, msg := range out.Messages {
				wg.Add(1)
				go func(m types.Message) {
					defer func() {
						<-sem     // Release slot when worker completes
						wg.Done()
					}()

					p.processMessage(ctx, m)
				}(msg)
			}

			// If SQS returned fewer than requested messages, release unused acquired slots
			for i := 0; i < (10 - len(out.Messages)); i++ {
				<-sem
			}
		}
	}
}

func (p *Processor) processMessage(ctx context.Context, msg types.Message) {
	// 1. Execute LLM API Call with context deadline
	// 2. On success, DeleteMessage from SQS
	// 3. On failure, leave message for SQS Redrive Policy / DLQ
}