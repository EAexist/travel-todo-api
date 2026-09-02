ifeq ($(OS),Windows_NT)
    GRADLE := $(CURDIR)/gradlew.bat
    DOCKER_HOST_ENV := DOCKER_HOST=npipe:////./pipe/docker_engine
    ifneq (,$(wildcard .env.dev))
        include .env.dev
        export
    endif
else
    GRADLE := ./gradlew
    DOCKER_HOST_ENV :=
    ifneq (,$(wildcard .env.dev))
        include .env.dev
        export
    endif
endif

GIT_COMMIT := $(shell git rev-parse --short HEAD)

DOCKER := docker
DOCKER_COMPOSE_DEV := $(DOCKER) compose -f compose.dev.db.yml
DB_COMPOSE_TEST := $(DOCKER) compose -f compose.test.db.yml

PROFILES ?=
TEST_FILTER ?=

TEST_ID ?=
N_ITERATIONS ?= 5
SCRIPT ?=

.PHONY: help run-dev load-data schema-gen test-lambda test bootRun deploy-lambda test-db-up test-db-down latency-test load-test

help: ## Show this help message
	@echo "Usage: make [target]"

gemini-live-latency-test: ## Run Gemini API live latency test
	@cd llm-mock/api-latency-approximation && \
	export $$(grep -v '^#' .env | xargs) && \
	source .venv/Scripts/activate && \
	python measure_latency.py

bootRun: ## Run the application with DB and env vars
	@$(DOCKER_COMPOSE_DEV) up -d && \
	export $$(grep -v '^#' .env.dev | xargs) && \
	export $$(grep -v '^#' .env.dev.db | xargs) && \
	$(GRADLE) bootRun -x test --args='--spring.profiles.active=dev,$(PROFILES)'

load-data: ## Load reference data
	@$(GRADLE) bootRun --args='--spring.profiles.active=dev'

schema-gen: ## Generate DB schema using Hibernate
	@$(DB_COMPOSE) up -d
	@$(GRADLE) bootRun -x test --args='--spring.profiles.active=dev,schema-generation'
	@$(DB_COMPOSE) down

lambda-deploy: ## Build and deploy lambda with CDK
	@lambda-build
aws-lambda-deploy: aws-lambda-build ## Deploy to AWS Lambda with CDK
	@cd infra && cdk deploy

lambda-build:
	$(GRADLE) clean buildLambdaWebAdapterZip -x test

lambda-zip-test-run:
	@rm -rf /tmp/lambda-test
	@unzip build/distributions/*.zip -d /tmp/lambda-test
	@cd /tmp/lambda-test && \
	export $$(grep -v '^#' $(CURDIR)/.env.dev | xargs) && \
	export $$(grep -v '^#' $(CURDIR)/.env.dev.db | xargs) && \
	./run.sh
aws-lambda-build: ## Build AWS Lambda version
    @$(GRADLE) clean buildLambdaWebAdapterZip

aws-lambda-artifact-run: ## Test lambda artifact locally
	@bash scripts/test-lambda-docker.sh

test: ##Usage: make test PROFILES=... TEST_FILTER=...
	$(GRADLE) test \
		-Dspring.profiles.active=$(PROFILES) \
		$(if $(TEST_FILTER),--tests "$(TEST_FILTER)")

test-db-up:
	@echo "Starting test database..."
	$(DB_COMPOSE_TEST) up -d --wait

test-db-down:
	@$(DB_COMPOSE_TEST) down -t 1

load-test-bootrun:
	@$(DOCKER_COMPOSE_DEV) up -d && \
	export $$(grep -v '^#' .env.dev | xargs) && \
	export $$(grep -v '^#' .env.dev.db | xargs) && \
	export $$(grep -v '^#' .env.load-test | xargs) && \
	$(GRADLE) bootRun -x test --args='--spring.profiles.active=dev,no-security,load-test'

load-test:
	@$(GRADLE) clean build -x test
	@docker build -t travel-todo-api:$(GIT_COMMIT) .
	@cd load-test && \
	python -m scripts.run --test-id $(TEST_ID) --n-iterations $(N_ITERATIONS) --script $(SCRIPT) --target-tag $(GIT_COMMIT)
