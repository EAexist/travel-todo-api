package main

import (
	"fmt"
	"log"
	"os"
	"path/filepath"

	"github.com/joho/godotenv"
)

type Config struct {
	GammaModelPatametersPath string
	SamplesManifestPath      string
	Port                     string
	ChatCompletionsPath      string
}

func LoadConfig() (Config, error) {
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, using system env variables")
	}

	fixturesRoot := os.Getenv("FIXTURES_ROOT")
	if fixturesRoot == "" {
		return Config{}, fmt.Errorf("FIXTURES_ROOT is required")
	}

	gammaModelPatametersPath := os.Getenv("GAMMA_MODEL_PATAMETERS_PATH")
	if gammaModelPatametersPath == "" {
		return Config{}, fmt.Errorf("GAMMA_MODEL_PATAMETERS_PATH is required")
	}

	samplesManifestPath := os.Getenv("SAMPLES_MANIFEST_PATH")
	if samplesManifestPath == "" {
		return Config{}, fmt.Errorf("SAMPLES_MANIFEST_PATH is required")
	}

	port := os.Getenv("PORT")
	if port == "" {
		return Config{}, fmt.Errorf("PORT is required")
	}

	chatCompletionsPath := os.Getenv("CHAT_COMPLETIONS_PATH")
	if chatCompletionsPath == "" {
		return Config{}, fmt.Errorf("CHAT_COMPLETIONS_PATH is required")
	}

	return Config{
		GammaModelPatametersPath: filepath.Join(fixturesRoot, gammaModelPatametersPath),
		SamplesManifestPath:      filepath.Join(fixturesRoot, samplesManifestPath),
		Port:                     port,
		ChatCompletionsPath:      chatCompletionsPath,
	}, nil
}
