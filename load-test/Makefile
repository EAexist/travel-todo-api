.PHONY: bench-architecture-msg-broker bench-data-model-reservation-baseline llm-latency-approx

# Default values for arguments
TEST_ID ?=
TAG ?=
N_ITERATIONS ?= 2
SCRIPT ?= 000-smoke-test.js

# Main
bench-architecture-msg-broker:
	@python -m bench-architecture-msg-broker.run
	
bench-architecture-msg-broker-dev:
	@ENV=dev python -m bench-architecture-msg-broker.run

run-prometheus:
	@docker start prometheus

llm-latency-approx:
	@cd llm-mock-server/llm-latency-approximation && \
    source .venv/Scripts/activate && \
    python measure_latency.py

bench-data-model-reservation-baseline:
	@python -m bench-data-model-reservation.run --test-id $(TEST_ID) --target-tag bench-data-model-reservation-baseline

bench-data-model-reservation-target:
	@python -m bench-data-model-reservation.run --test-id $(TEST_ID) --target-tag bench-data-model-reservation-target

k6-test:
	node --test bench-architecture-msg-broker/k6/scripts/handleSummary.test.js

# Dev
docker-build-alloy-with-curl:
	@cd alloy && \
	docker build . -t grafana-alloy-with-curl:latest

BASE_DIR ?=
cANDIDATE_DIR ?=
OUTPUT ?=

bench-architecture-msg-broker-report:
	@python -m bench-architecture-msg-broker.report --base-dir $(BASE_DIR) --candidate-dir $(CANDIDATE_DIR) --output $(OUTPUT)

bench-data-model-reservation-report:
	@python -m bench-data-model-reservation.report --test-id $(TEST_ID)