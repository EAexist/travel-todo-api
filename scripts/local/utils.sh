CONTAINER_NAME="db"  
DB_USER="guest"          
DB_NAME="trip-todo"     

function run_sql() {
    ARG=$1

    docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME $ARG
}

function load_table_from_csv() {

    if [ "$#" -ne 3 ]; then
        echo "Error: process_file requires 3 arguments: <TARGET_TABLE> <CSV_FILE_LOCAL> <SQL_FILE_LOCAL>."
        return 1 # Return a non-zero status to indicate error
    fi

    TARGET_TABLE=$1
    CSV_FILE_LOCAL=$2    
    SQL_FILE_LOCAL=$3 

    CSV_FILE_TEMP_CONTAINER="/tmp/$TARGET_TABLE.csv"

    # 1. Clear the Airport table (Run DDL)
    # echo "1. Clearing Airport table..."
    # docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "TRUNCATE TABLE airport RESTART IDENTITY CASCADE;"

    # if [ $? -ne 0 ]; then
    #     echo "ERROR: Failed to clear table."
    #     exit 1
    # fi

    # 1. Copy CSV data into the container
    echo "1. Copying processed CSV data and SQL script to container at ${CSV_FILE_TEMP_CONTAINER}..."
    docker cp ${CSV_FILE_LOCAL} ${CONTAINER_NAME}:${CSV_FILE_TEMP_CONTAINER}
    # docker cp ${SQL_FILE_LOCAL} ${CONTAINER_NAME}:${SQL_PATH_CONTAINER}

    # 2. Load data using \COPY (The clean CSV file columns now match the DB columns by order)
    echo "2. Loading data into $TARGET_TABLE table using COPY..."

    docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME < "${SQL_FILE_LOCAL}"
        # -v TARGET_TABLE=${TARGET_TABLE} \
        # -v CSV_FILE_TEMP_CONTAINER=${CSV_FILE_TEMP_CONTAINER} \
       

    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to load data into the table."
        exit 1
    fi

    # 3. Clean up temporary file
    rm ${TEMP_CSV_FILE}
    docker exec $CONTAINER_NAME sh -c "rm \"${CSV_FILE_TEMP_CONTAINER}\""
    echo "3. Cleanup complete."

    # 4. Verify data
    echo "4. Verifying data:"
    docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "SELECT * FROM $TARGET_TABLE LIMIT 10;"
}