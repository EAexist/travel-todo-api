import csv
import sys
import os

# --- Configuration ---
CSV_FILE_PATH = 'data/todo_preset_stock_todo_content.csv'
SQL_OUTPUT_PATH = 'V_INSERT_TODO_PRESET_STOCK_TODO_CONTENT.sql' # Your final migration file name
TABLE_NAME = 'temp_type_list'

# Column names from your CSV header (must match the TEMP table columns)
COLUMNS = [
    'todo_preset_type', 
    'index_key', 
    'stock_todo_content_type', 
    'is_flagged_to_add'
]

# --- Functions ---

def escape_sql_value(value, is_numeric=False):
    """Escapes single quotes and handles NULL/numeric formatting."""
    value = str(value).strip()
    if not value or value.upper() == 'NULL':
        return 'NULL'
    
    if is_numeric:
        # For SMALLINT/Numeric, just return the number
        return value
    
    # For VARCHAR/Text, escape single quotes and wrap
    return f"'{value.replace("'", "''")}'"

def generate_insert_values():
    """Reads the CSV and generates the list of SQL VALUES tuples."""
    sql_values = []
    
    try:
        with open(CSV_FILE_PATH, mode='r', newline='', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            
            for row in reader:
                # 1. Escape todo_preset_type (VARCHAR)
                preset_type = escape_sql_value(row.get('todo_preset_type'))
                
                # 2. Escape index_key (SMALLINT/Numeric)
                index_key = escape_sql_value(row.get('index_key'), is_numeric=True)
                
                # 3. Escape stock_todo_content_type (VARCHAR)
                content_type = escape_sql_value(row.get('stock_todo_content_type'))
                
                # 4. Escape is_flagged_to_add (BOOLEAN)
                is_flagged = row.get('is_flagged_to_add', 'FALSE').strip().upper() in ['TRUE', '1', 'T']
                
                # Format: ('value1', value2, 'value3', TRUE/FALSE)
                values = [
                    preset_type,
                    index_key,
                    content_type,
                    str(is_flagged).upper()
                ]
                
                # Format: (value1, value2, value3, value4)
                sql_values.append(f"({', '.join(values)})")
                
    except FileNotFoundError:
        print(f"Error: CSV file not found at {CSV_FILE_PATH}")
        sys.exit(1)
    
    if not sql_values:
        raise ValueError("No data rows found in CSV file. Aborting SQL generation.")

    # Generate the full INSERT statement part
    insert_prefix = f"INSERT INTO {TABLE_NAME} ({', '.join(COLUMNS)}) VALUES\n"
    return insert_prefix + ",\n".join(sql_values) + ";"


# --- Execution ---

if __name__ == "__main__":
    try:
        generated_inserts = generate_insert_values()
    except ValueError as e:
        print(f"❌ Failed to generate SQL: {e}")
        sys.exit(1)
    
    # --- Template for your full SQL script ---
    full_sql_template = f"""
-- Flyway migration generated from {CSV_FILE_PATH}

TRUNCATE TABLE todo_preset_stock_todo_content CASCADE;

CREATE TEMP TABLE {TABLE_NAME} (
    todo_preset_type VARCHAR(255),
    index_key SMALLINT,
    stock_todo_content_type VARCHAR(255),
    is_flagged_to_add BOOLEAN
);

-- START: Data Inserted from CSV
{generated_inserts}
-- END: Data Inserted from CSV

INSERT INTO todo_preset_stock_todo_content 
    ("todo-preset_id", "stock-todo-content_id", "is_flagged_to_add", "order_key") 
SELECT 
    tp.id AS "todo-preset_id",
    stc.id AS "stock-todo-content_id",
    ttl.is_flagged_to_add AS "is_flagged_to_add",
    ttl.index_key AS "order_key"
FROM 
    temp_type_list ttl
JOIN 
    todo_preset tp ON tp.type = ttl.todo_preset_type
JOIN 
    stock_todo_content stc ON stc.type = ttl.stock_todo_content_type
ON CONFLICT DO NOTHING;

DROP TABLE temp_type_list;

-- Note: The \\q command is unnecessary and must be removed for Flyway.
"""

    with open(SQL_OUTPUT_PATH, 'w', encoding='utf-8') as outfile:
        outfile.write(full_sql_template.strip())
        
    print(f"✅ Successfully generated complete Flyway migration script to {SQL_OUTPUT_PATH}")