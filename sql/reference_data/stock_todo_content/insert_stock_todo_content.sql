-- \set csv_file_path '/tmp/stock_todo_content.csv'
-- \set target_table 'stock_todo_content'
-- TRUNCATE TABLE stock_todo_content CASCADE;

CREATE TEMP TABLE staging_stock_todo_content (
    "type" VARCHAR(255),
    category VARCHAR(255),
    title VARCHAR(255),
    icon JSON
);

\COPY staging_stock_todo_content ("type", category, title, icon) FROM '/tmp/stock_todo_content.csv' DELIMITER ',' CSV HEADER;

INSERT INTO stock_todo_content ("type", category, title, icon) 
SELECT 
    s.type,
    s.category,
    s.title,
    s.icon
FROM staging_stock_todo_content s
ON CONFLICT ("type") 
DO UPDATE SET
    category = EXCLUDED.category,
    title = EXCLUDED.title,
    icon = EXCLUDED.icon;

DROP TABLE staging_stock_todo_content;
\q