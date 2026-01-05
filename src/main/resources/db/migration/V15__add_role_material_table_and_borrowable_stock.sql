-- V15: Create role_material table and add borrowable_stock to materials
-- Purpose: Support role-based material access control and track borrowable inventory

-- Add borrowable_stock column to materials if it doesn't exist
ALTER TABLE materials
ADD COLUMN IF NOT EXISTS borrowable_stock INTEGER NOT NULL DEFAULT 0;

-- Create role_material association table if it doesn't exist
CREATE TABLE IF NOT EXISTS role_material (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL,
    material_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_material UNIQUE (role_id, material_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_role_material_role ON role_material(role_id);
CREATE INDEX IF NOT EXISTS idx_role_material_material ON role_material(material_id);
