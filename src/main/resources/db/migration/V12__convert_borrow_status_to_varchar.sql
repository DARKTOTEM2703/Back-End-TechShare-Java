-- Migration V12: Ensure borrow status is VARCHAR (already done in V1 for PostgreSQL)
-- This migration is a no-op for PostgreSQL since V1 already creates status as VARCHAR

-- Verify the column exists and is VARCHAR
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'borrow' AND column_name = 'status' AND data_type = 'character varying'
    ) THEN
        RAISE EXCEPTION 'Column status should be VARCHAR, check V1 migration';
    END IF;
END $$;
