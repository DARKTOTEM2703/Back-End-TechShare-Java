-- V10__migrate_money_to_decimal.sql
-- Migración para convertir columnas monetarias de DOUBLE a DECIMAL(10,2)
-- PostgreSQL syntax

ALTER TABLE materials
    ALTER COLUMN price TYPE DECIMAL(10,2),
    ALTER COLUMN price SET DEFAULT 0.00,
    ALTER COLUMN price SET NOT NULL;

ALTER TABLE details_borrow
    ALTER COLUMN unit_price TYPE DECIMAL(10,2),
    ALTER COLUMN unit_price SET DEFAULT 0.00,
    ALTER COLUMN unit_price SET NOT NULL,
    ALTER COLUMN total_price TYPE DECIMAL(10,2),
    ALTER COLUMN total_price SET DEFAULT 0.00,
    ALTER COLUMN total_price SET NOT NULL;

ALTER TABLE borrow
    ALTER COLUMN amount TYPE DECIMAL(10,2),
    ALTER COLUMN amount SET DEFAULT 0.00,
    ALTER COLUMN amount SET NOT NULL;
