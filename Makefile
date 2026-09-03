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

DOCKER := docker
DB_COMPOSE := $(DOCKER) compose -f .docker/docker-compose-db-dev.yml
DB_COMPOSE_TEST := $(DOCKER) compose -f .docker/docker-compose-db-test.yml

PROFILES ?=
TEST_FILTER ?=

.PHONY: help run-dev load-data schema-gen test-lambda test bootRun deploy-lambda test-db-up test-db-down

help: ## Show this help message
	@echo "Usage: make [target]"

bootRun: ## Run the application with DB and env vars
	@$(DB_COMPOSE) up -d
	@export $$(grep -v '^#' .env.dev | xargs) && \
	export $$(grep -v '^#' .env.dev.db | xargs) && \
	$(GRADLE) bootRun -x test --args='--spring.profiles.active=dev,ssl,ai'

load-data: ## Load reference data
	@$(GRADLE) bootRun --args='--spring.profiles.active=dev'

schema-gen: ## Generate DB schema using Hibernate
	@$(DB_COMPOSE) up -d
	-@$(GRADLE) bootRun -x test --args='--spring.profiles.active=dev,schema-generation'
	@$(DB_COMPOSE) down

aws-lambda-deploy: aws-lambda-build ## Deploy to AWS Lambda with CDK
	@cd infra && cdk deploy

aws-lambda-build: ## Build AWS Lambda version
    @$(GRADLE) clean buildLambdaWebAdapterZip

aws-lambda-artifact-run: ## Test lambda artifact locally
	@bash scripts/test-lambda-docker.sh

test: ##Usage: make test PROFILES=... TEST_FILTER=...
	@export $$(grep -v '^#' .env.dev | xargs) && \
	$(GRADLE) test \
		-Dspring.profiles.active=$(PROFILES) \
		$(if $(TEST_FILTER),--tests "$(TEST_FILTER)")

test-db-up:
	@echo "Starting test database..."
	$(DB_COMPOSE_TEST) up -d --wait

test-db-down:
	@$(DB_COMPOSE_TEST) down -t 1

ci-test:
	@$(GRADLE) test \
		-Dspring.profiles.active=ci
