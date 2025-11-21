#!/bin/bash

DB_COMPOSE_FILE="docker-compose-db.yml"

docker compose -f "$DB_COMPOSE_FILE" up -d && \
DB_ENDPOINT=localhost DB_NAME=travel_todo_db DB_USER=guest DB_PASSWORD=hello_guest GOOGLE_APPLICATION_CREDENTIALS=./src/main/resources/certs/gc-service-account-key.json \
./gradlew clean bootRun -x test --args='--spring.profiles.active=local,development'