-- Note: Variables like :DB_COLUMNS, :TARGET_TABLE, and :CONTAINER_TEMP_PATH
-- will be replaced by the calling bash script before execution.

-- 1. Create a temporary staging table
CREATE TEMP TABLE staging_airport (
    iata_code VARCHAR(3),
    airport_name TEXT,
    city_name TEXT,
    iso2Digit_nation_code VARCHAR(2)
);

-- 2. COPY data from the container's temporary CSV file into the staging table
-- Replace the column list if your target table columns differ.
\COPY staging_airport(iata_code, airport_name, city_name, iso2Digit_nation_code) FROM '/tmp/airport.csv' WITH (FORMAT CSV, HEADER TRUE, DELIMITER ',', ENCODING 'UTF8');

-- 3. Perform the Upsert (INSERT OR UPDATE) from staging to the target table
-- :TARGET_TABLE must have a UNIQUE constraint on iataCode.
INSERT INTO airport (iata_code, airport_name, city_name, iso2digit_nation_code)
SELECT 
    s.iata_code, 
    s.airport_name, 
    s.city_name, 
    s.iso2Digit_nation_code
FROM staging_airport s
ON CONFLICT (iata_code) 
DO UPDATE SET
    airport_name = EXCLUDED.airport_name,
    city_name = EXCLUDED.city_name,
    iso2Digit_nation_code = EXCLUDED.iso2Digit_nation_code;

-- 4. Clean up the temporary staging table
DROP TABLE staging_airport;
\q