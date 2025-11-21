import csv
import sys
import os

# --- Configuration ---
CSV_FILE_PATH = 'data/stock_todo_content.csv'
SQL_OUTPUT_PATH = 'V_INSERT_STOCK_TODO_CONTENT.sql' 
TEMP_TABLE_NAME = 'staging_stock_todo_content'
TARGET_TABLE_NAME = 'stock_todo_content'

# Mapping of CSV Header Name (Key) to Target SQL Column Name (Value)
# Note: 'type_key' in CSV maps to 'type' in SQL
CSV_TO_SQL_MAP = {
    'type_key': 'type',
    'category': 'category',
    'title': 'title',
    'subtitle': 'subtitle',
    'icon': 'icon'
}

# The ordered list of SQL columns for the INSERT INTO clause
COLUMNS = list(CSV_TO_SQL_MAP.values()) 

# --- Functions ---

def escape_sql_value(value):
    """Escapes single quotes and wraps the value in single quotes for SQL (TEXT/VARCHAR/JSON)."""
    value = str(value).strip()
    # Check for empty string/NULL indicator
    if not value or value.upper() in ('NULL', 'NONE', '{}'):
        return 'NULL'
    
    # Escape single quotes by doubling them, then wrap in single quotes
    return f"'{value.replace("'", "''")}'"

def generate_insert_values():
    """Reads the CSV and generates the list of SQL VALUES tuples for the staging table."""
    sql_values = []
    
    try:
        # Determine the order in which we need to read the CSV columns
        required_csv_headers = list(CSV_TO_SQL_MAP.keys())

        with open(CSV_FILE_PATH, mode='r', newline='', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            
            # Basic header validation
            if not all(h in reader.fieldnames for h in required_csv_headers):
                raise ValueError(f"CSV is missing required columns: {required_csv_headers}. Found: {reader.fieldnames}")

            for row in reader:
                values = []
                # Build the ordered list of values based on the SQL column order
                for csv_header in required_csv_headers:
                    # Fetch the value using the CSV header, then escape it for SQL
                    values.append(escape_sql_value(row.get(csv_header)))
                
                # Format: ('value1', 'value2', 'value3', 'value4', 'value5')
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
-- Replaces the problematic \\COPY command with direct SQL INSERTS.

-- The original TRUNCATE TABLE command is removed/commented out 
-- because data deletion should typically be handled in a separate script or rollback.
-- TRUNCATE TABLE {TARGET_TABLE_NAME} CASCADE;

-- 1. Create a temporary staging table
CREATE TEMP TABLE {TEMP_TABLE_NAME} (
    "type" VARCHAR(255),
    category VARCHAR(255),
    title VARCHAR(255),
    subtitle VARCHAR(255),
    icon JSON
);

-- 2. START: Pure SQL Data Inserted from CSV
{generated_inserts}
-- 2. END: Pure SQL Data Inserted from CSV

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
INSERT INTO {TARGET_TABLE_NAME} ("type", category, title, subtitle, icon) 
SELECT 
    s.type,
    s.category,
    s.title,
    s.subtitle,
    s.icon
FROM {TEMP_TABLE_NAME} s
ON CONFLICT ("type") 
DO UPDATE SET
    category = EXCLUDED.category,
    title = EXCLUDED.title,
    subtitle = EXCLUDED.subtitle,
    icon = EXCLUDED.icon;

-- 4. Clean up the temporary staging table
DROP TABLE {TEMP_TABLE_NAME};
"""

    with open(SQL_OUTPUT_PATH, 'w', encoding='utf-8') as outfile:
        outfile.write(full_sql_template.strip())
        
    print(f"✅ Successfully generated complete Flyway migration script: {SQL_OUTPUT_PATH}")