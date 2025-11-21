-- Flyway migration generated from data/stock_todo_content.csv.
-- Replaces the problematic \COPY command with direct SQL INSERTS.

-- The original TRUNCATE TABLE command is removed/commented out 
-- because data deletion should typically be handled in a separate script or rollback.
-- TRUNCATE TABLE stock_todo_content CASCADE;

-- 1. Create a temporary staging table
CREATE TEMP TABLE staging_stock_todo_content (
    "type" VARCHAR(255),
    category VARCHAR(255),
    title VARCHAR(255),
    subtitle VARCHAR(255),
    icon JSON
);

-- 2. START: Pure SQL Data Inserted from CSV
INSERT INTO staging_stock_todo_content (type, category, title, subtitle, icon) VALUES
('ACCOMODATION', 'RESERVATION', '숙박 예약', NULL, '{"name":"🛌","type":"tossface"}'),
('INSURANCE', 'WORK', '여행자 보험 가입', NULL, '{"name":"🛡️","type":"tossface"}'),
('PASSPORT', 'FOREIGN', '여권', NULL, '{"name":"passport","type":"material-community"}'),
('CASH', 'FOREIGN', '환전 (현금)', NULL, '{"name":"💱","type":"tossface"}'),
('PREPAID_CARD', 'FOREIGN', '환전 (충전식 카드)', NULL, '{"name":"money-check-alt","type":"font-awesome-5"}'),
('CREDIT_CARD', 'FOREIGN', '해외 사용 신용카드', NULL, '{"name":"credit-card","type":"font-awesome"}'),
('ROAMING', 'FOREIGN', '로밍', NULL, '{"name":"📶","type":"tossface"}'),
('VISIT_JAPAN', 'FOREIGN', 'Visit Japan', NULL, '{"name":"visit-japan","type":"image"}'),
('MILITARY_APPROVEMENT', 'FOREIGN', '국외여행허가', NULL, '{"name":"👨🏻‍✈️","type":"tossface"}'),
('CLOTHING', 'CLOTHING', '옷', NULL, '{"name":"👕","type":"tossface"}'),
('TOOTHBRUSH_SET', 'WASH', '칫솔, 치약', NULL, '{"name":"🪥","type":"tossface"}'),
('CHARGER', 'ELECTRONICS', '폰 충전기', NULL, '{"name":"charger","type":"image"}'),
('EARPHONES', 'ELECTRONICS', '이어폰', NULL, '{"name":"🎧","type":"tossface"}'),
('POWER_BANK', 'ELECTRONICS', '보조 배터리', NULL, '{"name":"power-bank","type":"image"}'),
('SUITCASE', 'SUPPLY', '캐리어', NULL, '{"name":"🧳","type":"tossface"}'),
('BAG', 'SUPPLY', '들고다닐 가방', NULL, '{"name":"🎒","type":"tossface"}'),
('SUNSCREEN', 'WASH', '선크림', NULL, '{"name":"shield-sun-outline","type":"material-community"}'),
('TISSUE', 'SUPPLY', '휴대용 티슈', NULL, '{"name":"🗳️","type":"tossface"}'),
('PERSONAL_MEDICATION', 'SUPPLY', '개인 복용약', NULL, '{"name":"💊","type":"tossface"}'),
('COSMETICS', 'WASH', '화장품', NULL, '{"name":"💅","type":"tossface"}'),
('TOWEL', 'WASH', '수건', NULL, '{"name":"towel","type":"image"}'),
('UMBRELLA', 'SUPPLY', '우산', NULL, '{"name":"☂️","type":"tossface"}'),
('CAMERA', 'SUPPLY', '카메라', NULL, '{"name":"📷","type":"tossface"}'),
('PAJAMAS', 'CLOTHING', '잠옷', NULL, '{"name":"pajamas","type":"image"}'),
('SANDALS', 'CLOTHING', '샌들', NULL, '{"name":"sandals","type":"image"}'),
('SUNGLASSES', 'SUPPLY', '선글라스', NULL, '{"name":"🕶️","type":"tossface"}'),
('TUMBLER', 'SUPPLY', '텀블러', NULL, '{"name":"🥤","type":"tossface"}'),
('ADAPTER', 'ELECTRONICS', '어댑터', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_JP', 'ELECTRONICS', '어댑터 (Type A)', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_US', 'ELECTRONICS', '어댑터 (Type B)', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_GB', 'ELECTRONICS', '어댑터 (Type G)', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_TH', 'ELECTRONICS', '어댑터 (Type A & B)', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_AU', 'ELECTRONICS', '어댑터 (Type I)', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_CN', 'ELECTRONICS', '어댑터 (Type A & I)', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_TW', 'ELECTRONICS', '어댑터 (Type A & B)', NULL, '{"name":"🔌","type":"tossface"}'),
('ADAPTER_PH', 'ELECTRONICS', '어댑터 (Type A & B)', NULL, '{"name":"🔌","type":"tossface"}');

-- 2. END: Pure SQL Data Inserted from CSV

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
INSERT INTO stock_todo_content ("type", category, title, subtitle, icon) 
SELECT 
    s.type,
    s.category,
    s.title,
    s.subtitle,
    s.icon
FROM staging_stock_todo_content s
ON CONFLICT ("type") 
DO UPDATE SET
    category = EXCLUDED.category,
    title = EXCLUDED.title,
    subtitle = EXCLUDED.subtitle,
    icon = EXCLUDED.icon;

-- 4. Clean up the temporary staging table
DROP TABLE staging_stock_todo_content;