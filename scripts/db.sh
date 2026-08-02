#!/bin/bash

# Load configuration
source ./scripts/config.sh

function check_dependencies() {
    command -v docker >/dev/null 2>&1 || { echo "Error: docker is not installed."; exit 1; }
    command -v csvcut >/dev/null 2>&1 || { echo "Error: csvcut (csvkit) is not installed."; exit 1; }
}
check_dependencies

function cleanup() {
    if [ -n "$CONTAINER_NAME" ]; then
        docker exec "${CONTAINER_NAME}" sh -c "rm -f /tmp/upsert_*.sql /tmp/*.csv"
    fi
}
trap cleanup EXIT

function load_table_from_csv() {
    if [ "$#" -ne 3 ]; then
        echo "Error: load_table_from_csv requires 3 arguments: <TARGET_TABLE> <CSV_FILE_LOCAL> <SQL_FILE_LOCAL>."
        return 1
    fi

    local TARGET_TABLE=$1
    local CSV_FILE_LOCAL=$2
    local SQL_FILE_LOCAL=$3

    local CSV_FILE_TEMP_CONTAINER="/tmp/$TARGET_TABLE.csv"
    local SQL_FILE_CONTAINER="/tmp/upsert_$TARGET_TABLE.sql"

    echo "1. Copying data to container..."
    docker cp "${CSV_FILE_LOCAL}" "${CONTAINER_NAME}:${CSV_FILE_TEMP_CONTAINER}"
    docker cp "${SQL_FILE_LOCAL}" "${CONTAINER_NAME}:${SQL_FILE_CONTAINER}"

    echo "2. Loading data into $TARGET_TABLE..."
    docker exec "${CONTAINER_NAME}" sh -c "psql -U \"$DB_USER\" -d \"$DB_NAME\" -f \"$SQL_FILE_CONTAINER\""

    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to load data into $TARGET_TABLE."
        exit 1
    fi

    echo "3. Verifying data:"
    docker exec "${CONTAINER_NAME}" psql -U "${DB_USER}" -d "${DB_NAME}" -c "SELECT * FROM $TARGET_TABLE LIMIT 10;"
}

function load_airport() {
    echo "Loading Airport data..."
    local TARGET_TABLE="airport"
    local CSV_FILE_LOCAL="./data/temp/airports_formatted.csv"
    local SQL_FILE_LOCAL="./sql/reference_data/airport/upsert_airports.sql"
    local CSV_FILE_RAW="./data/airports_sample.csv"

    # Pre-process
    csvcut --output-encoding UTF-8 -c "iataCode,airportName,cityName,iso2DigitNationCode" "${CSV_FILE_RAW}" > "${CSV_FILE_LOCAL}"
    
    load_table_from_csv "${TARGET_TABLE}" "${CSV_FILE_LOCAL}" "${SQL_FILE_LOCAL}"
}

function load_airline() {
    echo "Loading Airline data..."
    local TARGET_TABLE="airline"
    local CSV_FILE_LOCAL="./data/temp/airlines_formatted.csv"
    local SQL_FILE_LOCAL="./sql/reference_data/airline/upsert_airlines.sql"
    local CSV_FILE_RAW="./data/airlines_sample.csv"

    # Pre-process
    csvcut -c "airlineIataCode,airlineName_trimmed" "${CSV_FILE_RAW}" > "${CSV_FILE_LOCAL}"

    load_table_from_csv "${TARGET_TABLE}" "${CSV_FILE_LOCAL}" "${SQL_FILE_LOCAL}"
}

case "$1" in
    load-all)
        load_airport
        load_airline
        ;;
    load-airport)
        load_airport
        ;;
    load-airline)
        load_airline
        ;;
    *)
        echo "Usage: $0 {load-all|load-airport|load-airline}"
        exit 1
        ;;
esac
