source ./scripts/local/utils.sh

function load_stockTodoContent() {

    TARGET_TABLE="stock_todo_content"
    JSON_FILE_LOCAL="./data/stockTodoContent.json" 
    SQL_FILE_LOCAL="./sql/reference_data/stock_todo_content/insert_stock_todo_content.sql"

    CSV_FILE_LOCAL="./data/temp/stock-todo-content.csv"

    # 1. Check if the JSON file exists
    if [ ! -f "$JSON_FILE_LOCAL" ]; then
        echo "Error: JSON file '${JSON_FILE_LOCAL}' not found."
        exit 1
    fi

    echo "Starting JSON data import into the '$DB_NAME' database..."


    # 2. Define and save CSV header
    echo "type_key,category,title,subtitle,icon" > "$CSV_FILE_LOCAL"
    echo "-> CSV header saved to ${CSV_FILE_LOCAL}."


    # 3. Convert JSON Data to CSV Format and Append
    echo "-> Converting JSON data to CSV format and appending to file..."
    jq -r '.[] | 
        [
            .type, 
            .category, 
            .title, 
            .subtitle, 
            (.icon | tojson) 
        ] 
        | @csv
    ' "$JSON_FILE_LOCAL" | tr -d '\r' >> "$CSV_FILE_LOCAL"

    # 4. Validate jq execution result
    if [ $? -eq 0 ]; then
        echo "✅ Success: JSON data successfully converted to CSV format and saved to ${CSV_FILE_LOCAL}."
        echo "--- ${CSV_FILE_LOCAL} content (partial) ---"
        head -n 3 "$CSV_FILE_LOCAL"
        echo "----------------------------------------"
    else
        echo "❌ Failure: An error occurred during JSON conversion."
        exit 1
    fi

    load_table_from_csv ${TARGET_TABLE} ${CSV_FILE_LOCAL} ${SQL_FILE_LOCAL}
}

function load_todo_preset_stock_todo_content() {

    TARGET_TABLE="todo_preset_stock_todo_content"
    SQL_FILE_LOCAL="./sql/reference_data/todo_preset_stock_todo_content/insert_todo_preset_stock_todo_content.sql"
    CSV_FILE_LOCAL="./data/todo_preset_stock_todo_content.csv"
    load_table_from_csv ${TARGET_TABLE} ${CSV_FILE_LOCAL} ${SQL_FILE_LOCAL}
}

load_stockTodoContent
load_todo_preset_stock_todo_content