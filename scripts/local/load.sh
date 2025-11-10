CONTAINER_NAME="db"  # PostgreSQL 컨테이너 이름 (실제 이름으로 변경 필요)
DB_USER="guest"            # DB 사용자 이름
DB_NAME="trip-todo"       # DB 이름 (실제 이름으로 변경 필요)

function load_table_from_csv() {

    if [ "$#" -ne 3 ]; then
        echo "Error: process_file requires 3 arguments: <TARGET_TABLE> <CSV_FILE_LOCAL> <SQL_FILE_LOCAL>."
        return 1 # Return a non-zero status to indicate error
    fi

    TARGET_TABLE=$1
    CSV_FILE_LOCAL=$2    
    SQL_FILE_LOCAL=$3 

    CSV_FILE_TEMP_CONTAINER="/tmp/$TARGET_TABLE.csv"
    SQL_FILE_CONTAINER="/tmp/upsert_$TARGET_TABLEs.sql"

    # 1. Clear the Airport table (Run DDL)
    # echo "1. Clearing Airport table..."
    # docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "TRUNCATE TABLE airport RESTART IDENTITY CASCADE;"

    # if [ $? -ne 0 ]; then
    #     echo "ERROR: Failed to clear table."
    #     exit 1
    # fi

    # 1. Copy the processed CSV data into the container
    echo "1. Copying processed CSV data and SQL script to container at ${CSV_FILE_TEMP_CONTAINER}..."
    
    docker cp ${CSV_FILE_LOCAL} ${CONTAINER_NAME}:${CSV_FILE_TEMP_CONTAINER}
    echo "--- ${CSV_FILE_TEMP_CONTAINER} content (partial) ---"
    docker exec "$CONTAINER_NAME" sh -c "head -n 3 \"$CSV_FILE_TEMP_CONTAINER\""
    
    docker cp ${SQL_FILE_LOCAL} ${CONTAINER_NAME}:${SQL_FILE_CONTAINER}


    # 2. Load data using \COPY (The clean CSV file columns now match the DB columns by order)
    echo "2. Loading data into Airport table using COPY..."

    # docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -f "$SQL_FILE_CONTAINER"
    docker exec "$CONTAINER_NAME" sh -c "psql -U \"$DB_USER\" -d \"$DB_NAME\" -f \"$SQL_FILE_CONTAINER\""    # -v TARGET_TABLE=${TARGET_TABLE} \
        # -v CSV_FILE_TEMP_CONTAINER=${CSV_FILE_TEMP_CONTAINER} \

    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to load data into the table."
        exit 1
    fi

    # 3. Clean up temporary file
    docker exec $CONTAINER_NAME sh -c "rm \"${CSV_FILE_TEMP_CONTAINER}\" \"${SQL_FILE_CONTAINER}\""
    echo "3. Cleanup complete."

    # 4. Verify data
    echo "4. Verifying data:"
    docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "SELECT * FROM $TARGET_TABLE LIMIT 10;"
}

function load_airport() {

    TARGET_TABLE="airport"
    CSV_FILE_LOCAL="./data/temp/airports_formatted.csv"    
    SQL_FILE_LOCAL="./sql/reference_data/airport/upsert_airports.sql" 

    CSV_FILE_RAW="./data/airports_sample.csv"    
    echo "Pre-processing CSV: Selecting columns by name"
    csvcut --output-encoding UTF-8 -c "iataCode,airportName,cityName,iso2DigitNationCode" "${CSV_FILE_RAW}"> "${CSV_FILE_LOCAL}"
    sync
    echo "--- ${CSV_FILE_LOCAL} content (partial) ---"
    head -n 3 "$CSV_FILE_LOCAL"

    # if [ $? -ne 0 ]; then
    #     echo "ERROR: Failed to pre-process CSV using csvcut. Check csvkit installation."
    #     exit 1
    # fi

    load_table_from_csv ${TARGET_TABLE} ${CSV_FILE_LOCAL} ${SQL_FILE_LOCAL}
}

function load_airline() {
    
    TARGET_TABLE="airline"
    CSV_FILE_LOCAL="./data/temp/airlines_formatted.csv"    
    SQL_FILE_LOCAL="./sql/reference_data/airline/upsert_airlines.sql" 

    CSV_FILE_RAW="./data/airlines_sample.csv"    
    echo "Pre-processing CSV: Selecting columns by name"
    csvcut -c "airlineIataCode,airlineName_trimmed" "${CSV_FILE_RAW}" > "${CSV_FILE_LOCAL}"



    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to pre-process CSV using csvcut. Check csvkit installation."
        exit 1
    fi

    load_table_from_csv ${TARGET_TABLE} ${CSV_FILE_LOCAL} ${SQL_FILE_LOCAL}
}

load_airport
load_airline