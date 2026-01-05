-- V13: Añade columna image_path a sub_categories si no existe
ALTER TABLE sub_categories
    ADD COLUMN IF NOT EXISTS image_path VARCHAR(255);
