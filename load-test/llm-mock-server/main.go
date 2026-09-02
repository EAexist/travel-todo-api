package main

import (
	"encoding/csv"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"os"
	"strconv"
	"time"
)

const seed = 42

func main() {

	config, err := LoadConfig()
	if err != nil {
		log.Fatal("Could not load config: ", err)
	}

	// Parse CSV
	file, err := os.Open(config.GammaModelPatametersPath)
	if err != nil {
		log.Fatal("Could not open model file at ", config.GammaModelPatametersPath, ": ", err)
	}
	defer file.Close()

	reader := csv.NewReader(file)
	_, _ = reader.Read() // skip header
	records, err := reader.ReadAll()
	if err != nil {
		log.Fatal(err)
	}

	modelMap := make(map[string]GammaModel)
	for _, record := range records {
		id := record[0]
		alpha, _ := strconv.ParseFloat(record[1], 64)
		beta, _ := strconv.ParseFloat(record[2], 64)
		modelMap[id] = GammaModel{Alpha: alpha, Beta: beta}
	}

	sampler := NewLatencySampler(seed, modelMap)
	outputMatcher := NewOutputMatcher(config)

	geminiMockHandler := func(w http.ResponseWriter, req *http.Request) {
		body, err := io.ReadAll(req.Body)
		if err != nil {
			http.Error(w, "failed to read body", http.StatusBadRequest)
			return
		}
		start := time.Now()

		type Message struct {
			Content string `json:"content"`
		}
		type Payload struct {
			Messages []Message `json:"messages"`
		}

		var p Payload
		if err := json.Unmarshal(body, &p); err != nil {
			http.Error(w, "Invalid JSON payload", http.StatusBadRequest)
			return
		}

		if len(p.Messages) == 0 {
			http.Error(w, "Invalid Message structure", http.StatusBadRequest)
			return
		}

		prompt := p.Messages[0].Content

		id, err := extractDataId(prompt)
		if err != nil {
			http.Error(w, "Invalid Input: Missing Id in Sample", http.StatusBadRequest)
			return
		}

		targetLatency, err := sampler.getSimulatedLatency(id)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		output, err := outputMatcher.getOutput(id)
		if err != nil {
			http.Error(w, "Invalid Sample Id", http.StatusBadRequest)
			return
		}

		chatCompletion, err := BuildMockResponse(output)
		if err != nil {
			http.Error(w, "Failed to build response", http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		if err := json.NewEncoder(w).Encode(chatCompletion); err != nil {
			// handle error
		}

		latencyHistogram.Observe(targetLatency.Seconds())

		processingTime := time.Since(start)
		sleepTime := targetLatency - processingTime
		log.Printf("Processing with total latency ~%v.\nSleeping for time left.", targetLatency)

		if sleepTime > 0 {
			time.Sleep(sleepTime)
		}
	}

	http.HandleFunc("/", func(w http.ResponseWriter, req *http.Request) {
		log.Printf("404: %s %s", req.Method, req.URL.Path)
		http.NotFound(w, req)
	})

	http.HandleFunc("/health", func(w http.ResponseWriter, req *http.Request) {
		w.WriteHeader(http.StatusOK)
	})

	http.HandleFunc(config.ChatCompletionsPath, geminiMockHandler)

	log.Printf("Endpoints:\n%s\n%s", "/health", config.ChatCompletionsPath)

	port := config.Port

	log.Printf("Server listening on http://localhost%s\n", port)
	log.Fatal(http.ListenAndServe(port, nil))
}
