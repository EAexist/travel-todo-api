package ai

import (
	"context"
	"fmt"
	"reservation-worker/internal/config"

	"github.com/openai/openai-go/v3"
	"github.com/openai/openai-go/v3/option"
)

type Client struct {
	genClient openai.Client
	model     string
}

func NewClient(ctx context.Context, cfg config.Config) (*Client, error) {

	// clientConfig := &genai.ClientConfig{
	// 	APIKey:  cfg.GenAiAPIKey,
	// 	Backend: genai.BackendGeminiAPI,
	// }

	// if cfg.GenAiClientBaseURL != nil {
	// 	clientConfig.HTTPOptions.BaseURL = *cfg.GenAiClientBaseURL
	// }

	client := openai.NewClient(
		option.WithAPIKey(cfg.GenAiAPIKey),
		option.WithBaseURL(*cfg.GenAiClientBaseURL))

	return &Client{
		genClient: client,
		model:     cfg.GenAimodel,
	}, nil
}

func (c *Client) GenerateContent(ctx context.Context, prompt string) (string, error) {

	resp, err := c.genClient.Chat.Completions.New(ctx, openai.ChatCompletionNewParams{
		Model: c.model,
		Messages: []openai.ChatCompletionMessageParamUnion{
			openai.UserMessage(prompt),
		},
	})
	if err != nil {
		return "", fmt.Errorf("failed to generate content: %w", err)
	}

	if len(resp.Choices) == 0 {
		return "", fmt.Errorf("no content generated")
	}

	return resp.Choices[0].Message.Content, nil
}

func (c *Client) Close() {
	// c.genClient.clo
}
