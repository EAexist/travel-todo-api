-- Note: Variables like :DB_COLUMNS, :TARGET_TABLE, and :CONTAINER_TEMP_PATH
-- will be replaced by the calling bash script before execution.

-- 1. Create a temporary staging table
CREATE TEMP TABLE staging_airline (
    icao_code VARCHAR(3),
    iata_code VARCHAR(3),
    title TEXT
);

-- 2. COPY data from the container's temporary CSV file into the staging table
-- Replace the column list if your target table columns differ.
\COPY staging_airline(icao_code, iata_code, title) FROM '/tmp/airline.csv' WITH (FORMAT CSV, HEADER TRUE, DELIMITER ',', ENCODING 'UTF8');

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
-- :TARGET_TABLE must have a UNIQUE constraint on iataCode.
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
\q