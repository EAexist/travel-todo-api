import csv
import sys
import os

# --- Configuration ---
CSV_FILE_PATH = 'data/airlines_sample.csv'
SQL_OUTPUT_PATH = 'V_INSERT_AIRLINE_DATA.sql' 
TEMP_TABLE_NAME = 'staging_airline'
TARGET_TABLE_NAME = 'airline'

# Mapping of CSV Header Name (Key) to Target SQL Column Name (Value)
# Note: You only care about icao_code, iata_code, and title for the target table.
CSV_TO_SQL_MAP = {
    'airlineIcaoCode': 'icao_code',
    'airlineIataCode': 'iata_code',
    'airlineName': 'title'
    # 'airlineName_trimmed' is ignored as it's not used in the SQL template
}

# The ordered list of columns in the temporary SQL table (used for the INSERT statement)
SQL_COLUMNS_ORDERED = list(CSV_TO_SQL_MAP.values()) # ['icao_code', 'iata_code', 'title']

# --- Functions ---

def escape_sql_value(value):
    """Escapes single quotes and wraps the value in single quotes for SQL (TEXT/VARCHAR)."""
    value = str(value).strip()
    if not value or value.upper() == 'NULL':
        # Treat empty or NULL string as SQL NULL
        return 'NULL'
    
    # Escape single quotes by doubling them, then wrap in single quotes
    return f"'{value.replace("'", "''")}'"

def generate_insert_values():
    """Reads the CSV and generates the list of SQL VALUES tuples for the staging table."""
    sql_values = []
    
    try:
        with open(CSV_FILE_PATH, mode='r', newline='', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            
            # Ensure the CSV contains the required source columns
            required_csv_headers = list(CSV_TO_SQL_MAP.keys())
            if not all(h in reader.fieldnames for h in required_csv_headers):
                raise ValueError(f"CSV is missing required columns: {required_csv_headers}. Found: {reader.fieldnames}")

            for row in reader:
                # Build the ordered list of values for the staging table
                values = []
                for csv_header in required_csv_headers:
                    # Look up the value from the CSV row and apply SQL escaping
                    values.append(escape_sql_value(row.get(csv_header)))
                
                # Format: ('value1', 'value2', 'value3')
                sql_values.append(f"({', '.join(values)})")
                
    except FileNotFoundError:
        print(f"Error: CSV file not found at {CSV_FILE_PATH}")
        sys.exit(1)
    
    if not sql_values:
        raise ValueError("No data rows found in CSV file. Aborting SQL generation.")

    # Generate the full INSERT statement prefix
    insert_prefix = f"INSERT INTO {TEMP_TABLE_NAME} ({', '.join(SQL_COLUMNS_ORDERED)}) VALUES\n"
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
-- NOTE: CSV columns (airlineIcaoCode, airlineIataCode, airlineName) mapped to target columns.

-- 1. Create a temporary staging table
CREATE TEMP TABLE {TEMP_TABLE_NAME} (
    icao_code VARCHAR(3),
    iata_code VARCHAR(3),
    title TEXT
);

-- 2. START: Pure SQL Data Inserted from CSV
{generated_inserts}
-- 2. END: Pure SQL Data Inserted from CSV

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
-- Note: ON CONFLICT is on icao_code as per your script.
INSERT INTO {TARGET_TABLE_NAME} ({', '.join(SQL_COLUMNS_ORDERED)})
SELECT 
    s.icao_code,
    s.iata_code, 
    s.title
FROM {TEMP_TABLE_NAME} s
ON CONFLICT (icao_code) 
DO UPDATE SET
    iata_code = EXCLUDED.iata_code,
    title = EXCLUDED.title;

-- 4. Clean up the temporary staging table
DROP TABLE {TEMP_TABLE_NAME};

-- Note: The \\q command is unnecessary and removed for Flyway compatibility.
"""

    with open(SQL_OUTPUT_PATH, 'w', encoding='utf-8') as outfile:
        outfile.write(full_sql_template.strip())
        
    print(f"✅ Successfully generated complete Flyway migration script: {SQL_OUTPUT_PATH}")