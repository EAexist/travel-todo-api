package main

import (
	"fmt"
	"math"
	"math/rand"
	"sync"
	"time"
)

type GammaModel struct {
	Alpha float64
	Beta  float64
}

type LatencySampler struct {
	modelMap map[string]GammaModel
	rng      *rand.Rand
	mu       sync.Mutex
}

func NewLatencySampler(seed int64, modelMap map[string]GammaModel) *LatencySampler {
	return &LatencySampler{
		modelMap: modelMap,
		rng:      rand.New(rand.NewSource(seed)),
	}
}

func (s *LatencySampler) getSimulatedLatency(id string) (time.Duration, error) {

	model, ok := s.modelMap[id]

	if !ok {
		return 0, fmt.Errorf("Model not found for sample %s", id)
	}

	return s.SampleGamma(model.Alpha, model.Beta), nil
}

func (s *LatencySampler) SampleGamma(alpha, beta float64) time.Duration {

	s.mu.Lock()
	defer s.mu.Unlock()

	// Marsaglia and Tsang method for Gamma distribution
	d := alpha - 1.0/3.0
	c := 1.0 / math.Sqrt(9.0*d)
	for {
		x := s.rng.NormFloat64()
		v := 1.0 + c*x
		if v <= 0 {
			continue
		}
		v = math.Pow(v, 3)
		u := s.rng.Float64()
		if u < 1.0-0.331*(x*x)*(x*x) {
			return time.Duration(d*v*beta) * time.Second
		}
		if math.Log(u) < 0.5*x*x*d*(1.0-v+math.Log(v)) {
			return time.Duration(d*v*beta) * time.Second
		}
	}
}
