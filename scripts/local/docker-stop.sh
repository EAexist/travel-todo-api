#!/bin/bash

DB_COMPOSE_FILE="docker-compose-db.yml"
APP_COMPOSE_FILE="docker-compose.yml"

echo "Stopping and removing existing containers..."
docker compose -f "$DB_COMPOSE_FILE" -f "$APP_COMPOSE_FILE" stop