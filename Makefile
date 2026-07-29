ifeq ($(OS),Windows_NT)
    GRADLE := ./gradlew.bat
    ifneq (,$(wildcard .env.dev))
        include .env.dev
        export
    endif
else
    GRADLE := ./gradlew
    ifneq (,$(wildcard .env.dev))
        include .env.dev
        export
    endif
endif

DOCKER := docker
DB_COMPOSE := $(DOCKER) compose -f docker-compose-db.yml

PROFILES ?=
TEST_FILTER ?=

# Compute active profiles: always start with dev, append if PROFILES is set
ifeq ($(PROFILES),)
    ACTIVE_PROFILES := local
else
    ACTIVE_PROFILES := local,$(PROFILES)
endif

.PHONY: help run-dev load-data schema-gen test-lambda test

help: ## Show this help message
	@echo "Usage: make [target]"

run-dev: ## Run the application in dev modeNotFoundException
	@$(GRADLE) bootRun -x test --args='--spring.profiles.active=dev,ai'

load-data: ## Load reference data
	@$(GRADLE) bootRun --args='--spring.profiles.active=dev'

schema-gen: ## Generate DB schema using Hibernate
	@$(DB_COMPOSE) up -d
	-@$(GRADLE) bootRun -x test --args='--spring.profiles.active=dev,schema-generation'
	@$(DB_COMPOSE) down

test-lambda: ## Build and test lambda artifact locally
	$(GRADLE) clean buildLambdaWebAdapterZip -x test
	@bash scripts/test-lambda-docker.sh

test: ## Run tests. Usage: make test [PROFILES=...] [TEST_FILTER=...]
	@$(DB_COMPOSE) up -d
	-@$(GRADLE) clean test -Dspring.profiles.active=$(ACTIVE_PROFILES) $(if $(TEST_FILTER),--tests "$(TEST_FILTER)")
	@$(DB_COMPOSE) down