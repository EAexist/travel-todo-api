#!/bin/bash

# Load environment variables from .env.grafana-cloud
if [ -f .env.grafana-cloud ]; then
    export $(grep -v '^#' .env.grafana-cloud | xargs)
else
    echo "Error: .env file not found."
    exit 1
fi
