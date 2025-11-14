#!/bin/bash

DB_COMPOSE_FILE="docker-compose-db.yml"

docker compose -f "$DB_COMPOSE_FILE" up -d && gradle clean bootRun -x test --args='--spring.profiles.active=local'