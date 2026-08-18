-- Flyway migration generated from airlines_sample.csv.
-- NOTE: CSV columns (airlineIcaoCode, airlineIataCode, airlineName) mapped to target columns.

-- 1. Create a temporary staging table
CREATE
TEMP TABLE staging_airline (
    icao_code VARCHAR(3),
    iata_code VARCHAR(3),
    title TEXT
);

-- 2. START: Pure SQL Data Inserted from CSV
INSERT INTO staging_airline (icao_code, iata_code, title)
VALUES ('EOK', 'RF', '에어로 K'),
       ('ABL', 'BX', '에어 부산'),
       ('APZ', 'YP', '에어 프레미아'),
       ('ASV', 'RS', '에어 서울'),
       ('AAR', 'OZ', '아시아나 항공'),
       ('ESR', 'ZE', '이스타항공'),
       ('JJA', '7C', '제주 항공'),
       ('JNA', 'LJ', '진에어'),
       ('KAL', 'KE', '대한 항공'),
       ('TWB', 'TW', '티웨이 항공'),
       ('AIH', 'KJ', '에어 인천'),
       ('ADO', 'HD', '에어 두'),
       ('ANA', 'NH', '전일본공수'),
       ('FDA', 'JH', '후지드림 항공'),
       ('JAL', 'JL', '일본 항공'),
       ('JTA', 'NU', '저팬 트랜스오션 에어'),
       ('JJP', 'GK', '제트스타 일본'),
       ('APJ', 'MM', '피치 항공'),
       ('SKY', 'BC', '스카이마크 항공'),
       ('SNJ', '6J', '솔라시드 에어'),
       ('SFJ', '7G', '스타플라이어'),
       ('AJX', 'NQ', '에어재팬'),
       ('SJO', 'IJ', '일본 춘추 항공'),
       ('TZP', 'ZG', '집 에어'),
       ('AHX', 'MZ', '아마쿠사 항공'),
       ('AKX', 'EH', '아나 윙즈'),
       ('IBX', 'FW', '아이벡스 항공'),
       ('JAC', 'JC', '일본 항공 커뮤터'),
       ('ORC', 'OC', '오리엔탈 에어 브리지'),
       ('RAC', 'NU', '류큐 에어 커뮤터'),
       ('TOK', 'BV', '토키 항공');

-- 2. END: Pure SQL Data Inserted from CSV

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
-- Note: ON CONFLICT is on icao_code as per your script.
INSERT INTO airline (icao_code, iata_code, title)
SELECT s.icao_code,
       s.iata_code,
       s.title
FROM staging_airline s ON CONFLICT (icao_code)
DO
UPDATE SET
    iata_code = EXCLUDED.iata_code,
    title = EXCLUDED.title;

-- 4. Clean up the temporary staging table
DROP TABLE staging_airline;

-- Flyway migration generated from airports_sample.csv.
-- NOTE: CSV columns (airportName, cityName, iso2DigitNationCode, iataCode) mapped to target columns.

-- 1. Create a temporary staging table
CREATE
TEMP TABLE staging_airport (
    iata_code VARCHAR(3),
    airport_name TEXT,
    city_name TEXT,
    iso2Digit_nation_code VARCHAR(2)
);

-- 2. START: Pure SQL Data Inserted from CSV
INSERT INTO staging_airport (iata_code, airport_name, city_name, iso2Digit_nation_code)
VALUES ('AGJ', '아구니 공항', '아구니', 'JP'),
       ('AXT', '아키타 공항', '아키타', 'JP'),
       ('AXJ', '아마쿠사 비행장', '아마쿠사', 'JP'),
       ('ASJ', '아마미 공항', '아마미', 'JP'),
       ('AOJ', '아오모리 공항', '아오모리', 'JP'),
       ('AKJ', '아사히카와 공항', '아사히카와', 'JP'),
       ('CJJ', '청주국제공항', '청주', 'KR'),
       ('NGO', '주부 국제공항', '나고야', 'JP'),
       ('TAE', '대구국제공항', '대구', 'KR'),
       ('FUJ', '후쿠에 공항', '후쿠에', 'JP'),
       ('FUK', '후쿠오카 공항', '후쿠오카', 'JP'),
       ('FKS', '후쿠시마 공항', '후쿠시마', 'JP'),
       ('PUS', '김해국제공항', '부산', 'KR'),
       ('GMP', '김포국제공항', '서울', 'KR'),
       ('KUV', '군산 공항', '군산', 'KR'),
       ('KWJ', '광주공항', '광주', 'KR'),
       ('HAC', '하치조지마 공항', '하치조지마', 'JP'),
       ('HKD', '하코다테 공항', '하코다테', 'JP'),
       ('HNA', '하나마키 공항', '하나마키', 'JP'),
       ('HND', '하네다 공항', '도쿄', 'JP'),
       ('HIJ', '히로시마 공항', '히로시마', 'JP'),
       ('IBR', '이바라키공항', '이바라키', 'JP'),
       ('IKI', '이키 공항', '이키', 'JP'),
       ('ICN', '인천국제공항', '서울', 'KR'),
       ('IWJ', '이와미 공항', '이와미', 'JP'),
       ('IWO', '이오지마 공군기지', '이오지마', 'JP'),
       ('IZO', '이즈모 공항', '이즈모', 'JP'),
       ('CJU', '제주국제공항', '제주', 'KR'),
       ('DNA', '가데나 기지', '카데나', 'JP'),
       ('KOJ', '가고시마 공항', '가고시마', 'JP'),
       ('KIX', '간사이 국제공항', '오사카', 'JP'),
       ('KKX', '기카이 공항', '키카이', 'JP'),
       ('KTD', '기타다이토 공항', '키타다이토', 'JP'),
       ('KKJ', '기타큐슈 공항', '기타큐슈', 'JP'),
       ('UKB', '고베 공항', '오사카', 'JP'),
       ('KCZ', '고치 료마 공항', '코치', 'JP'),
       ('KMQ', '고마쓰 공항', '고마츠', 'JP'),
       ('KMJ', '구마모토 공항', '구마모토', 'JP'),
       ('UEO', '구메지마 공항', '쿠메지마', 'JP'),
       ('KUH', '구시로 공항', '구시로', 'JP'),
       ('IWK', '해병대 비행장 이와쿠니', '이와쿠니', 'JP'),
       ('MMJ', '마츠모토 공항', '마츠모토', 'JP'),
       ('MYJ', '마쓰야마 공항', '마츠야마', 'JP'),
       ('MMB', '메만베쓰 공항', '메만베츠', 'JP'),
       ('YGJ', '미호 요나고 공항', '요나고', 'JP'),
       ('MMD', '미나미다이토 공항', '미나미다이토', 'JP'),
       ('MSJ', '미사와공항', '미사와', 'JP'),
       ('MYE', '미야케지마 공항', '미야케지마', 'JP'),
       ('MMY', '미야코 공항', '미야코지마', 'JP'),
       ('KMI', '미야자키 공항', '미야자키', 'JP'),
       ('MBE', '몬베쓰 공항', '몬베츠', 'JP'),
       ('MWX', '무안 국제공항', '무안', 'KR'),
       ('NGS', '나가사키 공항', '나가사키', 'JP'),
       ('NKM', '나고야 비행장', '나고야', 'JP'),
       ('OKA', '나하 공항', '오키나와', 'JP'),
       ('SHB', '나카시베쓰 공항', '나카시베츠', 'JP'),
       ('SHM', '난키 시라하마 공항', '시라하마', 'JP'),
       ('NRT', '나리타 국제공항', '도쿄', 'JP'),
       ('CTS', '신치토세 공항', '삿포로', 'JP'),
       ('ISG', '신이시가키 공항', '이시가키', 'JP'),
       ('TNE', '신다네가시마 공항', '다네가시마', 'JP'),
       ('KIJ', '니가타 공항', '니가타', 'JP'),
       ('NTQ', '노토 공항', '와지마', 'JP'),
       ('ONJ', '오다테 노시로 공항', '오다테노시로', 'JP'),
       ('OIT', '오이타 공항', '오이타', 'JP'),
       ('OKD', '오카다마 공항', '삿포로', 'JP'),
       ('OKJ', '오카야마 공항', '오카야마', 'JP'),
       ('OKI', '오키 공항', '오키 섬', 'JP'),
       ('OKE', '오키노에라부 공항', '오키노에라부지마', 'JP'),
       ('OIR', '오쿠시리 공항', '오쿠시리', 'JP'),
       ('ITM', '오사카 국제공항', '오사카', 'JP'),
       ('OIM', '오시마 공항', '오시마', 'JP'),
       ('KPO', '포항공항', '포항', 'KR'),
       ('RIS', '리시리 공항', '리시리', 'JP'),
       ('HIN', '사천공항', '진주', 'KR'),
       ('SDS', '사도공항', '사도', 'JP'),
       ('HSG', '사가공항', '사거', 'JP'),
       ('SDJ', '센다이 공항', '센다이', 'JP'),
       ('SHI', '시모지시마 공항', '시모지시마', 'JP'),
       ('FSZ', '시즈오카 공항', '시즈오카', 'JP'),
       ('SYO', '쇼나이 공항', '쇼나이', 'JP'),
       ('TJH', '다지마 공항', '도요오카', 'JP'),
       ('TAK', '다카마쓰 공항', '다카마츠', 'JP'),
       ('TRA', '타라마 공항', '타라마', 'JP'),
       ('OBO', '도카치 오비히로 공항', '오비히로', 'JP'),
       ('TKN', '도쿠노시마 공항', '도쿠노시마', 'JP'),
       ('TKS', '도쿠시마 공항', '도쿠시마', 'JP'),
       ('TTJ', '돗토리 공항', '돗토리', 'JP'),
       ('TOY', '도야마 공항', '도야마', 'JP'),
       ('TSJ', '쓰시마 공항', '쓰시마', 'JP'),
       ('USN', '울산공항', '울산', 'KR'),
       ('WKJ', '왓카나이 공항', '왓카나이', 'JP'),
       ('WJU', '원주공항', '원주', 'KR'),
       ('KUM', '야쿠시마 공항', '야쿠시마', 'JP'),
       ('GAJ', '야마가타 공항', '야마가타', 'JP'),
       ('UBJ', '야마구치 우베 공항', '우베', 'JP'),
       ('YNY', '양양국제공항', '양양', 'KR'),
       ('YEC', '예천 공군기지', '예천', 'KR'),
       ('RSU', '여수/순천공항', '여수/순처', 'KR'),
       ('OGN', '요나구니 공항', '요나구니', 'JP'),
       ('RNJ', '요론 공항', '요론지마', 'JP');

-- 2. END: Pure SQL Data Inserted from CSV

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
INSERT INTO airport (iata_code, airport_name, city_name, iso2digit_nation_code)
SELECT s.iata_code,
       s.airport_name,
       s.city_name,
       s.iso2Digit_nation_code
FROM staging_airport s ON CONFLICT (iata_code)
DO
UPDATE SET
    airport_name = EXCLUDED.airport_name,
    city_name = EXCLUDED.city_name,
    iso2Digit_nation_code = EXCLUDED.iso2Digit_nation_code;

-- 4. Clean up the temporary staging table
DROP TABLE staging_airport;

-- Todo Preset

INSERT INTO todo_preset (id, title, type)
VALUES
    -- Assuming title is nullable or empty
    (gen_random_uuid(), NULL, 'DEFAULT'),
    (gen_random_uuid(), NULL, 'DOMESTIC'),
    (gen_random_uuid(), NULL, 'FOREIGN'),
    (gen_random_uuid(), NULL, 'JAPAN') ON CONFLICT (type) DO NOTHING;

-- Flyway migration generated from data/stock_todo_content.csv.

-- 1. Create a temporary staging table
CREATE
TEMP TABLE staging_stock_todo_content (
    "type" VARCHAR(255),
    category VARCHAR(255),
    title VARCHAR(255),
    subtitle VARCHAR(255),
    icon JSON
);

-- 2. START: Pure SQL Data Inserted from CSV
INSERT INTO staging_stock_todo_content (type, category, title, subtitle, icon)
VALUES ('ACCOMODATION', 'RESERVATION', '숙박 예약', NULL, '{"name":"🛌","type":"tossface"}'),
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
SELECT s.type,
       s.category,
       s.title,
       s.subtitle,
       s.icon
FROM staging_stock_todo_content s ON CONFLICT ("type")
DO
UPDATE SET
    category = EXCLUDED.category,
    title = EXCLUDED.title,
    subtitle = EXCLUDED.subtitle,
    icon = EXCLUDED.icon;

-- 4. Clean up the temporary staging table
DROP TABLE staging_stock_todo_content;


-- Flyway migration generated from todo_preset_stock_todo_content.csv

CREATE
TEMP TABLE temp_type_list (
    todo_preset_type VARCHAR(255),
    index_key SMALLINT,
    stock_todo_content_type VARCHAR(255),
    is_flagged_to_add BOOLEAN
);

-- START: Data Inserted from CSV
INSERT INTO temp_type_list (todo_preset_type, index_key, stock_todo_content_type, is_flagged_to_add)
VALUES ('DEFAULT', 0, 'ACCOMODATION', TRUE),
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
SELECT tp.id                 AS "todo-preset_id",
       stc.id                AS "stock-todo-content_id",
       ttl.is_flagged_to_add AS "is_flagged_to_add",
       ttl.index_key         AS "order_key"
FROM temp_type_list ttl
         JOIN
     todo_preset tp ON tp.type = ttl.todo_preset_type
         JOIN
     stock_todo_content stc ON stc.type = ttl.stock_todo_content_type ON CONFLICT DO NOTHING;

DROP TABLE temp_type_list;