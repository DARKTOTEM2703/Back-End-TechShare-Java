-- Migration V12: Convert borrow status from PostgreSQL ENUM to VARCHAR
-- This ensures compatibility with JPA EnumType.STRING

-- Step 1: Add new column with VARCHAR type
ALTER TABLE borrow ADD COLUMN status_temp VARCHAR(50);

-- Step 2: Copy data from ENUM column to VARCHAR column
UPDATE borrow SET status_temp = status::TEXT;

-- Step 3: Drop old ENUM column
ALTER TABLE borrow DROP COLUMN status;

-- Step 4: Rename new column to original name
ALTER TABLE borrow RENAME COLUMN status_temp TO status;

-- Step 5: Set NOT NULL constraint and default value
ALTER TABLE borrow ALTER COLUMN status SET NOT NULL;
ALTER TABLE borrow ALTER COLUMN status SET DEFAULT 'PENDING';

-- Step 6: Recreate the index
CREATE INDEX idx_borrow_status ON borrow(status);

-- Step 7: Drop the custom ENUM type if no other table uses it
DROP TYPE IF EXISTS borrow_status_enum;
