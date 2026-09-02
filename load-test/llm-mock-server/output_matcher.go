package main

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"regexp"
	"strings"
)

var customInstRegex = regexp.MustCompile(`(?s)<mock_data_id>(.*?)</<mock_data_id>`)

type OutputMatcher struct {
	config      Config
	idToContent map[string]string
}

func NewOutputMatcher(config Config) *OutputMatcher {
	r := &OutputMatcher{
		config:      config,
		idToContent: make(map[string]string),
	}
	r.loadOutputs()
	return r
}

func (r *OutputMatcher) loadOutputs() error {
	data, err := os.ReadFile(r.config.SamplesManifestPath)
	if err != nil {
		return err
	}

	type sample struct {
		ID         string `json:"id"`
		Path       string `json:"path"`
		OutputPath string `json:"output_path"`
	}
	type SampleManifest struct {
		Samples []sample `json:"samples"`
	}
	var sampleManifest SampleManifest

	if err := json.Unmarshal(data, &sampleManifest); err != nil {
		return err
	}

	samplesDir := filepath.Dir(r.config.SamplesManifestPath)

	for _, s := range sampleManifest.Samples {
		contentPath := filepath.Join(samplesDir, s.OutputPath)
		content, err := os.ReadFile(contentPath)
		if err != nil {
			return err
		}
		r.idToContent[s.ID] = string(content)
	}

	return nil
}

func (r *OutputMatcher) getOutput(id string) (string, error) {

	content, ok := r.idToContent[id]
	if !ok {
		return "", fmt.Errorf("content not found for id: %s", id)
	}

	return content, nil
}

func extractDataId(prompt string) (string, error) {
	matches := customInstRegex.FindStringSubmatch(prompt)
	if len(matches) > 1 {
		return strings.TrimSpace(matches[1]), nil
	}
	return "", fmt.Errorf("Id Missing in Sample Input")
}
