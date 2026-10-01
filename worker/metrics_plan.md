Yes. In Go, the closest equivalent to your Spring/Micrometer Timer is a Prometheus Histogram.

If you already have the start timestamp from the SQS message, you don't need a running timer object. Calculate:

duration := time.Since(startTime)

and observe that duration into a Prometheus histogram.

1. Define the histogram

Using the official Prometheus Go client:

var reservationAnalysisE2EDuration = prometheus.NewHistogramVec(
    prometheus.HistogramOpts{
        Name: "reservation_analysis_e2e_duration_seconds",
        Help: "End-to-end reservation analysis duration",
        Buckets: prometheus.DefBuckets,
    },
    []string{"stage_id"},
)

Register it:

func init() {
    prometheus.MustRegister(reservationAnalysisE2EDuration)
}

This is roughly equivalent to your Spring:

Timer.builder("reservation.analysis.e2e.duration")
    .description("End-to-end reservation analysis duration")
    .tag("stage_id", stageId)
    .publishPercentileHistogram()
    .register(this.registry);
2. At the end of processing

If your message contains something like:

startTime time.Time

then:

duration := time.Since(startTime)

reservationAnalysisE2EDuration.
    WithLabelValues(stageID).
    Observe(duration.Seconds())

So your worker flow becomes:

func (w *Worker) processMessage(ctx context.Context, msg Message) error {
    startTime := msg.StartTime
    stageID := msg.StageID

    // ... do reservation analysis ...

    duration := time.Since(startTime)

    reservationAnalysisE2EDuration.
        WithLabelValues(stageID).
        Observe(duration.Seconds())

    return nil
}
3. Expose /metrics

Your Go worker needs an HTTP endpoint for Prometheus to scrape:

http.Handle("/metrics", promhttp.Handler())

go func() {
    log.Println("metrics server listening on :8081")
    if err := http.ListenAndServe(":8081", nil); err != nil {
        log.Fatal(err)
    }
}()

Then Prometheus gets:

reservation_analysis_e2e_duration_seconds_bucket{stage_id="stage-1",le="0.1"} 12
reservation_analysis_e2e_duration_seconds_bucket{stage_id="stage-1",le="0.25"} 18
reservation_analysis_e2e_duration_seconds_bucket{stage_id="stage-1",le="0.5"} 25
...
reservation_analysis_e2e_duration_seconds_sum{stage_id="stage-1"} 8.42
reservation_analysis_e2e_duration_seconds_count{stage_id="stage-1"} 25
Important difference from Spring

Your Java code:

sample.stop(timer);

measures the time inside the JVM process.

Your Go code:

time.Since(startTime)

can measure the true end-to-end duration from the timestamp carried by the SQS message, even if the message spent time in SQS before the worker processed it.

So if your startTime was created when the reservation-analysis operation originally began, this is actually exactly what you want for your E2E metric.

For Prometheus/Grafana P95, you'd then use:

histogram_quantile(
  0.95,
  sum by (le) (
    rate(reservation_analysis_e2e_duration_seconds_bucket[5m])
  )
)

If you're trying to make the Go metric names/labels match your existing Spring metrics exactly so your existing Grafana/PromQL continues working, there's one additional detail: Prometheus converts the Java dotted metric name and Go metric name differently. I can show you the exact Go setup to make the resulting Prometheus series compatible with your existing reservation_analysis_e2e_duration metric.

shall i define this metric / timer handler as diff. module vs just add the lines directly inside the service

I'd make it a separate metrics module/package, not put the Prometheus definition directly inside your service.

For your worker, a clean structure would be:

internal/
├── reservation/
│   └── service.go
├── metrics/
│   └── metrics.go
└── sqs/
    └── worker.go
internal/metrics/metrics.go

Keep the Prometheus-specific stuff here:

package metrics

import "github.com/prometheus/client_golang/prometheus"

var ReservationAnalysisE2EDuration = prometheus.NewHistogramVec(
    prometheus.HistogramOpts{
        Name: "reservation_analysis_e2e_duration_seconds",
        Help: "End-to-end reservation analysis duration",
        Buckets: prometheus.DefBuckets,
    },
    []string{"stage_id"},
)

func init() {
    prometheus.MustRegister(ReservationAnalysisE2EDuration)
}

Then your service only does:

duration := time.Since(startTime)

metrics.ReservationAnalysisE2EDuration.
    WithLabelValues(stageID).
    Observe(duration.Seconds())
Even better: hide Prometheus from the service

If you want cleaner architecture, I'd actually expose a small method:

package metrics

type Metrics struct {
    reservationAnalysisE2E *prometheus.HistogramVec
}

func New() *Metrics {
    m := &Metrics{
        reservationAnalysisE2E: prometheus.NewHistogramVec(
            prometheus.HistogramOpts{
                Name: "reservation_analysis_e2e_duration_seconds",
                Help: "End-to-end reservation analysis duration",
                Buckets: prometheus.DefBuckets,
            },
            []string{"stage_id"},
        ),
    }

    prometheus.MustRegister(m.reservationAnalysisE2E)

    return m
}

func (m *Metrics) ObserveReservationAnalysisE2E(
    stageID string,
    duration time.Duration,
) {
    m.reservationAnalysisE2E.
        WithLabelValues(stageID).
        Observe(duration.Seconds())
}

Then service:

duration := time.Since(startTime)

w.metrics.ObserveReservationAnalysisE2E(stageID, duration)

I prefer this second approach for your worker because your service shouldn't need to know that the implementation is Prometheus. It just says:

"record reservation analysis E2E duration"

and the metrics package decides how that gets exported.

Also, since you're doing load testing/observability, I'd avoid creating/registering a new HistogramVec every time a message is processed. Create/register it once at worker startup, then call Observe() for every message.