package metrics

import (
	"net/http"
	"os"
	"time"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

// Metrics holds the prometheus metrics collectors
type Metrics struct {
	Registry *prometheus.Registry

	reservationAnalysisE2E       prometheus.Histogram
	reservationAnalysisCompleted prometheus.Counter
}

// New initializes and registers the metrics
func New() *Metrics {
	m := &Metrics{
		Registry: prometheus.NewRegistry(),

		reservationAnalysisE2E: prometheus.NewHistogram(
			prometheus.HistogramOpts{
				Name:    "reservation_analysis_e2e_duration_seconds",
				Help:    "End-to-end reservation analysis duration",
				Buckets: prometheus.DefBuckets,
			},
		),
		reservationAnalysisCompleted: prometheus.NewCounter(
			prometheus.CounterOpts{
				Name: "reservation_analysis_completed_count",
				Help: "Total number of successfully completed reservation analyses",
			},
		),
	}

	globalLabels := prometheus.Labels{
		"test_id": os.Getenv("MANAGEMENT_METRICS_TAGS_TEST_ID"),
	}

	registerer := prometheus.WrapRegistererWith(globalLabels, m.Registry)

	registerer.MustRegister(
		m.reservationAnalysisE2E,
		m.reservationAnalysisCompleted,
	)

	return m
}

func (m *Metrics) StartServer(addr string) error {
	mux := http.NewServeMux()

	mux.Handle("/metrics", promhttp.HandlerFor(
		m.Registry,
		promhttp.HandlerOpts{
			EnableOpenMetrics: true,
		},
	))

	return http.ListenAndServe(addr, mux)
}

// ObserveReservationAnalysisE2E records the duration of reservation analysis
func (m *Metrics) ObserveReservationAnalysisE2E(
	duration time.Duration,
) {
	m.reservationAnalysisE2E.Observe(duration.Seconds())
}

func (m *Metrics) ObserveReservationAnalysisCompleted() {
	m.reservationAnalysisCompleted.Inc()
}
