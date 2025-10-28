source ./scripts/local/utils.sh

function load_airport() {

    TARGET_TABLE="airport"
    SQL_FILE_LOCAL="./sql/reference_data/airport/upsert_airports.sql" 
    
    CSV_FILE_LOCAL="./data/temp/airports_formatted.csv"    

    CSV_FILE_RAW="./data/airports_sample.csv"    
    echo "Pre-processing CSV: Selecting columns by name"
    csvcut -c "iataCode,airportName,cityName,iso2DigitNationCode" "${CSV_FILE_RAW}" | iconv -f EUC-KR -t UTF-8 > "${CSV_FILE_LOCAL}"

    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to pre-process CSV using csvcut. Check csvkit installation."
        exit 1
    fi
    
    load_table_from_csv ${TARGET_TABLE} ${CSV_FILE_LOCAL} ${SQL_FILE_LOCAL}
}

function load_airline() {
    
    TARGET_TABLE="airline" 
    SQL_FILE_LOCAL="./sql/reference_data/airline/upsert_airlines.sql" 
    
    CSV_FILE_LOCAL="./data/temp/airlines_formatted.csv"   

    CSV_FILE_RAW="./data/airlines_sample.csv"    
    echo "Pre-processing CSV: Selecting columns by name"
    csvcut -c "airlineIcaoCode,airlineIataCode,airlineName_trimmed" "${CSV_FILE_RAW}" | iconv -f EUC-KR -t UTF-8 > "${CSV_FILE_LOCAL}"

    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to pre-process CSV using csvcut. Check csvkit installation."
        exit 1
    fi

    load_table_from_csv ${TARGET_TABLE} ${CSV_FILE_LOCAL} ${SQL_FILE_LOCAL}
}

# load_airport
load_airline