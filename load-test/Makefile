.PHONY: load-test llm-latency-approx

# Default values for arguments
TEST_ID ?= message-broker-benchmark
N_ITERATIONS ?= 5
SCRIPT ?= 002-breakpoint-test.js
TAG ?= latest

# Main
load-test:
	@python -m scripts.run --test-id $(TEST_ID) --n-iterations $(N_ITERATIONS) --script $(SCRIPT) --target-tag $(TAG)

smoke-test:
	@python -m scripts.run --test-id $(TEST_ID)-smoke --n-iterations $(N_ITERATIONS) --script 000-smoke-test.js --target-tag $(TAG)

probe-break-test:
	@python -m scripts.run --test-id $(TEST_ID)-break --n-iterations 1 --script 001-probe-breakpoint-test.js --target-tag $(TAG)

run-prometheus:
	@docker start prometheus

llm-latency-approx:
	@cd llm-mock-server/llm-latency-approximation && \
    source .venv/Scripts/activate && \
    python measure_latency.py

# Dev
test-run-windows-exporter:
	@python -c "import scripts.utils; scripts.utils.launch_host_memory_metrics_tracking(r\".\bin\windows_exporter-0.31.8-amd64.exe\")"

test-run-reporter:
	@STAGE_START_BUFFER_SECONDS=20 python -c "import scripts.reporter.reporter; scripts.reporter.reporter.report(\"$(TEST_ID)\", $(N_ITERATIONS))"



docker-build-alloy-with-curl:
	@cd alloy && \
	docker build . -t grafana-alloy-with-curl:latest


