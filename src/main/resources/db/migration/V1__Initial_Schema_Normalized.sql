-- PostgreSQL: Drop tables with CASCADE
DROP TABLE IF EXISTS verification_token CASCADE;
DROP TABLE IF EXISTS movements CASCADE;
DROP TABLE IF EXISTS details_borrow CASCADE;
DROP TABLE IF EXISTS borrow CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS user_role CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS favorites CASCADE;
DROP TABLE IF EXISTS materials CASCADE;
DROP TABLE IF EXISTS sub_categories CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================
-- CREATE SCHEMA
-- ============================================

-- Tabla: roles
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_roles_name ON roles(name);

-- Tabla: users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    profile_image_url VARCHAR(255),
    is_enabled BOOLEAN DEFAULT TRUE,
    birth_date DATE DEFAULT NULL,
    gender VARCHAR(20) DEFAULT NULL CHECK (gender IN ('Mujer', 'Hombre', 'Otro')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_enabled ON users(is_enabled);

-- Trigger para updated_at automático
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: categories
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_categories_name ON categories(name);
CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: sub_categories
CREATE TABLE sub_categories (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    CONSTRAINT uk_name_category UNIQUE (name, category_id)
);
CREATE INDEX idx_sub_categories_category ON sub_categories(category_id);
CREATE TRIGGER update_sub_categories_updated_at BEFORE UPDATE ON sub_categories
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: materials
CREATE TABLE materials (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    sub_category_id INTEGER DEFAULT NULL,
    image_path VARCHAR(255),
    created_by INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories (id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL
);
CREATE INDEX idx_materials_sub_category ON materials(sub_category_id);
CREATE INDEX idx_materials_created_by ON materials(created_by);
CREATE INDEX idx_materials_search ON materials USING gin(to_tsvector('spanish', name || ' ' || COALESCE(description, '')));
CREATE TRIGGER update_materials_updated_at BEFORE UPDATE ON materials
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: user_role
CREATE TABLE user_role (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);
CREATE INDEX idx_user_role_role ON user_role(role_id);
CREATE INDEX idx_user_role_user ON user_role(user_id);

-- Tabla: reviews
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    material_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_material_user UNIQUE (material_id, user_id)
);
CREATE INDEX idx_reviews_material ON reviews(material_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE TRIGGER update_reviews_updated_at BEFORE UPDATE ON reviews
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: favorites
CREATE TABLE favorites (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    material_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_material UNIQUE (user_id, material_id)
);
CREATE INDEX idx_favorites_user ON favorites(user_id);
CREATE INDEX idx_favorites_material ON favorites(material_id);

-- Tabla: borrow
CREATE TABLE borrow (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    resource_id INTEGER NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'BORROWED', 'RETURNED', 'OVERDUE', 'CANCELLED')),
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES materials (id) ON DELETE CASCADE
);
CREATE INDEX idx_borrow_user ON borrow(user_id);
CREATE INDEX idx_borrow_resource ON borrow(resource_id);
CREATE INDEX idx_borrow_status ON borrow(status);
CREATE INDEX idx_borrow_borrow_date ON borrow(borrow_date);
CREATE INDEX idx_borrow_return_date ON borrow(return_date);
CREATE TRIGGER update_borrow_updated_at BEFORE UPDATE ON borrow
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: details_borrow
CREATE TABLE details_borrow (
    id SERIAL PRIMARY KEY,
    borrow_id INTEGER NOT NULL,
    material_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (borrow_id) REFERENCES borrow (id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE RESTRICT
);
CREATE INDEX idx_details_borrow_borrow ON details_borrow(borrow_id);
CREATE INDEX idx_details_borrow_material ON details_borrow(material_id);
CREATE TRIGGER update_details_borrow_updated_at BEFORE UPDATE ON details_borrow
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: movements
CREATE TABLE movements (
    id SERIAL PRIMARY KEY,
    resource_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    move_type VARCHAR(50) NOT NULL CHECK (move_type IN ('BORROW', 'RETURN', 'PURCHASE', 'DONATION', 'ADJUSTMENT')),
    quantity INTEGER NOT NULL DEFAULT 1,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES materials (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_movements_resource ON movements(resource_id);
CREATE INDEX idx_movements_user ON movements(user_id);
CREATE INDEX idx_movements_move_type ON movements(move_type);
CREATE INDEX idx_movements_movement_date ON movements(movement_date);
CREATE TRIGGER update_movements_updated_at BEFORE UPDATE ON movements
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla: verification_token
CREATE TABLE verification_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_verification_token_token ON verification_token(token);
CREATE INDEX idx_verification_token_user ON verification_token(user_id);
CREATE INDEX idx_verification_token_expiry_date ON verification_token(expiry_date);

-- ============================================
-- INSERT INITIAL DATA
-- ============================================

-- Insert roles
INSERT INTO roles (id, name, description)
VALUES 
    (1, 'ADMIN', 'Administrador del sistema'),
    (2, 'USER', 'Usuario regular')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description;

-- Resetear secuencia después del insert con ID explícito
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));

-- Insert admin user
-- Email: jafethgamboabaas@gmail.com
-- Password: Jafeth_270703
-- Hash: $2a$10$G2QZUq7/UBQFAJu/ynIiWeYUqRkxYGqWG5xXvPelgeywvOqCM/k9q
INSERT INTO users (username, email, password, first_name, last_name, is_enabled)
SELECT 'jafethgamboabaas', 'jafethgamboabaas@gmail.com', '$2a$10$G2QZUq7/UBQFAJu/ynIiWeYUqRkxYGqWG5xXvPelgeywvOqCM/k9q', 'Jafeth Daniel', 'Gamboa Baas', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'jafethgamboabaas@gmail.com'
);

-- Assign ADMIN role to user
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'jafethgamboabaas@gmail.com'
AND NOT EXISTS (
    SELECT 1 FROM user_role ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- Insert categories
INSERT INTO categories (name, description)
VALUES 
    ('Electrónica', 'Componentes y dispositivos electrónicos'),
    ('Hardware', 'Componentes de computadora'),
    ('Accesorios', 'Accesorios varios'),
    ('Software', 'Licencias y software');

-- Insert subcategories
INSERT INTO sub_categories (category_id, name, description)
VALUES 
    (1, 'Microcontroladores', 'Arduino, ESP32, STM32'),
    (1, 'Sensores', 'Sensores de temperatura, humedad, distancia'),
    (2, 'Procesadores', 'CPUs y GPUs'),
    (2, 'Memoria', 'RAM, SSD, HDD'),
    (3, 'Cables', 'Cables USB, HDMI, Ethernet'),
    (3, 'Conectores', 'Conectores y adaptadores');
