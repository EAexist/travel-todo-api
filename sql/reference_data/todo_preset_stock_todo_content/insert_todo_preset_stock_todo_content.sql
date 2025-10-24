TRUNCATE TABLE todo_preset_stock_todo_content CASCADE;

CREATE TEMP TABLE temp_type_list (
    todo_preset_type VARCHAR(255),
    index_key SMALLINT,
    stock_todo_content_type VARCHAR(255),
    is_flagged_to_add BOOLEAN
);

\COPY temp_type_list (todo_preset_type, index_key, stock_todo_content_type, is_flagged_to_add) FROM '/tmp/todo_preset_stock_todo_content.csv' DELIMITER ',' CSV HEADER;

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

\q