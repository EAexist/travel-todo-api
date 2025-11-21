-- Flyway migration generated from data/airlines_sample.csv.
-- NOTE: CSV columns (airlineIcaoCode, airlineIataCode, airlineName) mapped to target columns.

-- 1. Create a temporary staging table
CREATE TEMP TABLE staging_airline (
    icao_code VARCHAR(3),
    iata_code VARCHAR(3),
    title TEXT
);

-- 2. START: Pure SQL Data Inserted from CSV
INSERT INTO staging_airline (icao_code, iata_code, title) VALUES
('EOK', 'RF', '에어로 K'),
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
SELECT 
    s.icao_code,
    s.iata_code, 
    s.title
FROM staging_airline s
ON CONFLICT (icao_code) 
DO UPDATE SET
    iata_code = EXCLUDED.iata_code,
    title = EXCLUDED.title;

-- 4. Clean up the temporary staging table
DROP TABLE staging_airline;

-- Note: The \q command is unnecessary and removed for Flyway compatibility.