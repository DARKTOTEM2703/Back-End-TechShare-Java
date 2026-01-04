-- Migration V11: Add borrowable_stock column to materials table
-- This column tracks how many units of a material are available for borrowing

ALTER TABLE materials
ADD COLUMN borrowable_stock INTEGER NOT NULL DEFAULT 0;

-- Set initial borrowable_stock equal to current stock for existing materials
UPDATE materials SET borrowable_stock = stock WHERE borrowable_stock = 0;

-- Add check constraint to ensure borrowable_stock doesn't exceed stock
ALTER TABLE materials
ADD CONSTRAINT chk_borrowable_stock_lte_stock CHECK (borrowable_stock <= stock);

-- Add index for queries filtering by borrowable stock
CREATE INDEX idx_materials_borrowable_stock ON materials(borrowable_stock);
