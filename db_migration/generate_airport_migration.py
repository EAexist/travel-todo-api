import csv
import sys
import os

# --- Configuration ---
# NOTE: The CSV file name must match what you have locally
CSV_FILE_PATH = 'data/airports_sample.csv' 
SQL_OUTPUT_PATH = 'V_INSERT_AIRPORT_DATA.sql' 
TEMP_TABLE_NAME = 'staging_airport'
TARGET_TABLE_NAME = 'airport'

# MAPPING: Maps the CSV Header Name (Key) to the Target SQL Column Name (Value).
# The order of the values in the map ensures the correct SQL INSERT order.
CSV_TO_SQL_MAP = {
    # CSV Header         : Target SQL Column
    'iataCode': 'iata_code',
    'airportName': 'airport_name',
    'cityName': 'city_name',
    'iso2DigitNationCode': 'iso2Digit_nation_code'
}

# The final ordered list of SQL columns for the INSERT INTO clause
COLUMNS = list(CSV_TO_SQL_MAP.values()) 

# --- Functions ---

def escape_sql_value(value):
    """Escapes single quotes and wraps the value in single quotes for SQL (TEXT/VARCHAR)."""
    value = str(value).strip()
    if not value or value.upper() == 'NULL':
        return 'NULL'
    
    # Escape single quotes by doubling them, then wrap in single quotes
    return f"'{value.replace("'", "''")}'"

def generate_insert_values():
    """Reads the CSV and generates the list of SQL VALUES tuples for the staging table."""
    sql_values = []
    
    try:
        with open(CSV_FILE_PATH, mode='r', newline='', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            
            # Check if all required headers exist in the CSV file
            required_csv_headers = list(CSV_TO_SQL_MAP.keys())
            if not all(h in reader.fieldnames for h in required_csv_headers):
                raise ValueError(f"CSV is missing required columns: {required_csv_headers}. Found: {reader.fieldnames}")
            
            for row in reader:
                values = []
                # Iterate through the required CSV headers in the order needed for the SQL table
                for csv_header in required_csv_headers:
                    # Fetch the value using the CSV header, then escape it for SQL
                    values.append(escape_sql_value(row.get(csv_header)))
                
                # Format: ('value1', 'value2', 'value3', 'value4')
                sql_values.append(f"({', '.join(values)})")
                
    except FileNotFoundError:
        print(f"Error: CSV file not found at {CSV_FILE_PATH}")
        sys.exit(1)
    
    if not sql_values:
        raise ValueError("No data rows found in CSV file. Aborting SQL generation.")

    # Generate the full INSERT statement part
    insert_prefix = f"INSERT INTO {TEMP_TABLE_NAME} ({', '.join(COLUMNS)}) VALUES\n"
    return insert_prefix + ",\n".join(sql_values) + ";\n"

# --- Execution ---

if __name__ == "__main__":
    try:
        generated_inserts = generate_insert_values()
    except ValueError as e:
        print(f"❌ Failed to generate SQL: {e}")
        sys.exit(1)
    
    # --- Template for your full SQL script ---
    full_sql_template = f"""
-- Flyway migration generated from {CSV_FILE_PATH}.
-- NOTE: CSV columns (airportName, cityName, iso2DigitNationCode, iataCode) mapped to target columns.

-- 1. Create a temporary staging table
CREATE TEMP TABLE {TEMP_TABLE_NAME} (
    iata_code VARCHAR(3),
    airport_name TEXT,
    city_name TEXT,
    iso2Digit_nation_code VARCHAR(2)
);

-- 2. START: Pure SQL Data Inserted from CSV
{generated_inserts}
-- 2. END: Pure SQL Data Inserted from CSV

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
INSERT INTO {TARGET_TABLE_NAME} (iata_code, airport_name, city_name, iso2digit_nation_code)
SELECT 
    s.iata_code, 
    s.airport_name, 
    s.city_name, 
    s.iso2Digit_nation_code
FROM {TEMP_TABLE_NAME} s
ON CONFLICT (iata_code) 
DO UPDATE SET
    airport_name = EXCLUDED.airport_name,
    city_name = EXCLUDED.city_name,
    iso2Digit_nation_code = EXCLUDED.iso2Digit_nation_code;

-- 4. Clean up the temporary staging table
DROP TABLE {TEMP_TABLE_NAME};

-- Note: The \\q command is unnecessary and removed for Flyway compatibility.
"""

    with open(SQL_OUTPUT_PATH, 'w', encoding='utf-8') as outfile:
        outfile.write(full_sql_template.strip())
        
    print(f"✅ Successfully generated complete Flyway migration script: {SQL_OUTPUT_PATH}")