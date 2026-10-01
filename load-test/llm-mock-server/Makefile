docker-build:
	@docker build . -t llm-mock-server:latest

run:
	@set -a; MSYS_NO_PATHCONV=1 . ./.env.dev; set +a; go run .