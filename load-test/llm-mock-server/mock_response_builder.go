package main

import "github.com/openai/openai-go/v3"

func BuildMockResponse(content string) (openai.ChatCompletion, error) {

	chatCompletion := openai.ChatCompletion{
		Choices: []openai.ChatCompletionChoice{
			{
				Message: openai.ChatCompletionMessage{
					Role:    "assistant",
					Content: content,
				},
			},
		},
	}

	return chatCompletion, nil
}
