docker-build:
	@docker build . -t reservation-data-processor:latest

run:
	@set -a; . ./.env.dev; set +a; go run ./cmd/worker