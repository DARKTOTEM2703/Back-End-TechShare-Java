-- V10__migrate_money_to_decimal.sql
-- Migración para convertir columnas monetarias de DOUBLE a DECIMAL(10,2)
-- Ajustar según dialecto MySQL/MariaDB

ALTER TABLE materials
    MODIFY COLUMN price DECIMAL(10,2) NOT NULL DEFAULT 0.00;

ALTER TABLE details_borrow
    MODIFY COLUMN unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN total_price DECIMAL(10,2) NOT NULL DEFAULT 0.00;

ALTER TABLE borrow
    MODIFY COLUMN amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- Nota: Si las columnas están en DOUBLE, MySQL realizará la conversión de forma automática.
-- Recomiendo ejecutar en entorno de staging primero y respaldar la base de datos antes de aplicar.
