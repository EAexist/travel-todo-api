#!/bin/bash

DB_COMPOSE_FILE="docker-compose-db.yml"
APP_COMPOSE_FILE="docker-compose.yml"

# --- 1. Clean up old conflicting containers first ---
echo "Stopping existing containers..."
docker compose -f "$DB_COMPOSE_FILE" -f "$APP_COMPOSE_FILE" stop

# --- 2. Build and Start all services in a single project ---
echo "Starting services from $DB_COMPOSE_FILE and $APP_COMPOSE_FILE..."
docker compose -f "$DB_COMPOSE_FILE" -f "$APP_COMPOSE_FILE" up -d

echo "Done! Services are running."