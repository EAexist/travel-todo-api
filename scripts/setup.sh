#!/bin/bash

# Load configuration
source ./scripts/config.sh

echo "Setting up development environment..."
docker compose -f "$DB_COMPOSE_FILE" up -d
echo "Environment is ready."
