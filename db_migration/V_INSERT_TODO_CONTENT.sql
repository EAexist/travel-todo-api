-- Flyway migration generated from todo_preset_stock_todo_content.csv

TRUNCATE TABLE todo_preset_stock_todo_content CASCADE;

CREATE TEMP TABLE temp_type_list (
    todo_preset_type VARCHAR(255),
    index_key SMALLINT,
    stock_todo_content_type VARCHAR(255),
    is_flagged_to_add BOOLEAN
);

-- START: Data Inserted from CSV
INSERT INTO temp_type_list (todo_preset_type, index_key, stock_todo_content_type, is_flagged_to_add) VALUES
('DEFAULT', 0, 'ACCOMODATION', TRUE),
('DEFAULT', 1, 'PASSPORT', FALSE),
('DEFAULT', 2, 'CASH', FALSE),
('DEFAULT', 3, 'PREPAID_CARD', FALSE),
('DEFAULT', 4, 'CREDIT_CARD', FALSE),
('DEFAULT', 5, 'ROAMING', FALSE),
('DEFAULT', 6, 'VISIT_JAPAN', FALSE),
('DEFAULT', 7, 'MILITARY_APPROVEMENT', FALSE),
('DEFAULT', 8, 'INSURANCE', FALSE),
('DEFAULT', 9, 'CLOTHING', FALSE),
('DEFAULT', 10, 'TOOTHBRUSH_SET', FALSE),
('DEFAULT', 11, 'CHARGER', FALSE),
('DEFAULT', 12, 'EARPHONES', FALSE),
('DEFAULT', 13, 'POWER_BANK', FALSE),
('DEFAULT', 14, 'ADAPTER', FALSE),
('DEFAULT', 15, 'SUITCASE', FALSE),
('DEFAULT', 16, 'BAG', FALSE),
('DEFAULT', 17, 'SUNSCREEN', FALSE),
('DEFAULT', 18, 'TISSUE', FALSE),
('DEFAULT', 19, 'PERSONAL_MEDICATION', FALSE),
('DEFAULT', 20, 'COSMETICS', FALSE),
('DEFAULT', 21, 'TOWEL', FALSE),
('DEFAULT', 22, 'UMBRELLA', FALSE),
('DEFAULT', 23, 'CAMERA', FALSE),
('DEFAULT', 24, 'PAJAMAS', FALSE),
('DEFAULT', 25, 'SANDALS', FALSE),
('DEFAULT', 26, 'SUNGLASSES', FALSE),
('DEFAULT', 27, 'TUMBLER', FALSE),
('DOMESTIC', 0, 'ACCOMODATION', TRUE),
('DOMESTIC', 1, 'INSURANCE', FALSE),
('DOMESTIC', 2, 'CLOTHING', FALSE),
('DOMESTIC', 3, 'TOOTHBRUSH_SET', FALSE),
('DOMESTIC', 4, 'CHARGER', FALSE),
('DOMESTIC', 5, 'EARPHONES', FALSE),
('DOMESTIC', 6, 'POWER_BANK', FALSE),
('DOMESTIC', 7, 'SUITCASE', FALSE),
('DOMESTIC', 8, 'BAG', FALSE),
('DOMESTIC', 9, 'SUNSCREEN', FALSE),
('DOMESTIC', 10, 'TISSUE', FALSE),
('DOMESTIC', 11, 'PERSONAL_MEDICATION', FALSE),
('DOMESTIC', 12, 'COSMETICS', FALSE),
('DOMESTIC', 13, 'TOWEL', FALSE),
('DOMESTIC', 14, 'UMBRELLA', FALSE),
('DOMESTIC', 15, 'CAMERA', FALSE),
('DOMESTIC', 16, 'PAJAMAS', FALSE),
('DOMESTIC', 17, 'SANDALS', FALSE),
('DOMESTIC', 18, 'SUNGLASSES', FALSE),
('DOMESTIC', 19, 'TUMBLER', FALSE),
('FOREIGN', 0, 'ACCOMODATION', TRUE),
('FOREIGN', 1, 'PASSPORT', TRUE),
('FOREIGN', 2, 'CASH', TRUE),
('FOREIGN', 3, 'ROAMING', TRUE),
('FOREIGN', 4, 'PREPAID_CARD', FALSE),
('FOREIGN', 5, 'CREDIT_CARD', FALSE),
('FOREIGN', 6, 'VISIT_JAPAN', FALSE),
('FOREIGN', 7, 'MILITARY_APPROVEMENT', FALSE),
('FOREIGN', 8, 'INSURANCE', FALSE),
('FOREIGN', 9, 'ADAPTER', TRUE),
('FOREIGN', 10, 'CLOTHING', FALSE),
('FOREIGN', 11, 'TOOTHBRUSH_SET', FALSE),
('FOREIGN', 12, 'CHARGER', FALSE),
('FOREIGN', 13, 'EARPHONES', FALSE),
('FOREIGN', 14, 'POWER_BANK', FALSE),
('FOREIGN', 15, 'SUITCASE', FALSE),
('FOREIGN', 16, 'BAG', FALSE),
('FOREIGN', 17, 'SUNSCREEN', FALSE),
('FOREIGN', 18, 'TISSUE', FALSE),
('FOREIGN', 19, 'PERSONAL_MEDICATION', FALSE),
('FOREIGN', 20, 'COSMETICS', FALSE),
('FOREIGN', 21, 'TOWEL', FALSE),
('FOREIGN', 22, 'UMBRELLA', FALSE),
('FOREIGN', 23, 'CAMERA', FALSE),
('FOREIGN', 24, 'PAJAMAS', FALSE),
('FOREIGN', 25, 'SANDALS', FALSE),
('FOREIGN', 26, 'SUNGLASSES', FALSE),
('FOREIGN', 27, 'TUMBLER', FALSE),
('JAPAN', 0, 'ACCOMODATION', TRUE),
('JAPAN', 1, 'PASSPORT', TRUE),
('JAPAN', 2, 'CASH', TRUE),
('JAPAN', 3, 'ROAMING', TRUE),
('JAPAN', 4, 'VISIT_JAPAN', TRUE),
('JAPAN', 5, 'PREPAID_CARD', FALSE),
('JAPAN', 6, 'CREDIT_CARD', FALSE),
('JAPAN', 7, 'MILITARY_APPROVEMENT', FALSE),
('JAPAN', 8, 'INSURANCE', FALSE),
('JAPAN', 9, 'ADAPTER_JP', TRUE),
('JAPAN', 10, 'CLOTHING', FALSE),
('JAPAN', 11, 'TOOTHBRUSH_SET', FALSE),
('JAPAN', 12, 'CHARGER', FALSE),
('JAPAN', 13, 'EARPHONES', FALSE),
('JAPAN', 14, 'POWER_BANK', FALSE),
('JAPAN', 15, 'SUITCASE', FALSE),
('JAPAN', 16, 'BAG', FALSE),
('JAPAN', 17, 'SUNSCREEN', FALSE),
('JAPAN', 18, 'TISSUE', FALSE),
('JAPAN', 19, 'PERSONAL_MEDICATION', FALSE),
('JAPAN', 20, 'COSMETICS', FALSE),
('JAPAN', 21, 'TOWEL', FALSE),
('JAPAN', 22, 'UMBRELLA', FALSE),
('JAPAN', 23, 'CAMERA', FALSE),
('JAPAN', 24, 'PAJAMAS', FALSE),
('JAPAN', 25, 'SANDALS', FALSE),
('JAPAN', 26, 'SUNGLASSES', FALSE),
('JAPAN', 27, 'TUMBLER', FALSE);
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

-- Note: The \q command is unnecessary and must be removed for Flyway.