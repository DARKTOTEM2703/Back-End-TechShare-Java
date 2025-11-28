SET FOREIGN_KEY_CHECKS = 0;

-- Normalized names
-- eliminar versiones previas (normalizadas/legacy)
DROP TABLE IF EXISTS user_roles;

DROP TABLE IF EXISTS user_role;

DROP TABLE IF EXISTS reviews;

DROP TABLE IF EXISTS favorites;

DROP TABLE IF EXISTS materials;

DROP TABLE IF EXISTS sub_categories;

DROP TABLE IF EXISTS categories;

DROP TABLE IF EXISTS roles;

DROP TABLE IF EXISTS users;
-- Legacy names (si existen, intentamos eliminarlas antes de crear las nuevas tablas)
DROP TABLE IF EXISTS usuario_role;

DROP TABLE IF EXISTS usuario;

DROP TABLE IF EXISTS role;

-- Reactivar comprobaciones de FK
SET FOREIGN_KEY_CHECKS = 1;

-- PASO 2: Crear tablas en snake_case (FORMA CORRECTA)

-- Tabla: roles
CREATE TABLE roles (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    profile_image_url VARCHAR(255),
    is_enabled BOOLEAN DEFAULT TRUE,
    birth_date DATE DEFAULT NULL,
    -- Guardamos género como ENUM para mantener compatibilidad con datos legacy
    gender ENUM('Mujer', 'Hombre', 'Otro') DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_enabled (is_enabled)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: categories
CREATE TABLE categories (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: sub_categories (ANTES "subCategories" - AHORA NORMALIZADA)
CREATE TABLE sub_categories (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    UNIQUE KEY uk_name_category (name, category_id),
    INDEX idx_category (category_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: materials
CREATE TABLE materials (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    sub_category_id INT NULL DEFAULT NULL,
    image_path VARCHAR(255),
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Permitimos NULL por defecto y en cascada de borrado dejamos NULL para evitar problemas
    -- durante migraciones/recreaciones del esquema.
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories (id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    INDEX idx_sub_category (sub_category_id),
    INDEX idx_created_by (created_by),
    FULLTEXT INDEX ft_search (name, description)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- La entidad `UsuarioRole` existe en el código y espera una columna 'id' auto_increment.
-- Para evitar que Hibernate intente alterar la tabla y falle, creamos la tabla con
-- una columna id auto_increment como clave primaria y añadimos una restricción
-- UNIQUE sobre (user_id, role_id) para preservar unicidad de pares.
CREATE TABLE IF NOT EXISTS user_role (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    INDEX idx_role (role_id),
    INDEX idx_user (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: reviews
CREATE TABLE reviews (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    material_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (
        rating >= 1
        AND rating <= 5
    ),
    comment VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY uk_material_user (material_id, user_id),
    INDEX idx_material (material_id),
    INDEX idx_user (user_id),
    INDEX idx_rating (rating)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: favorites
CREATE TABLE favorites (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    material_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_material (user_id, material_id),
    INDEX idx_user (user_id),
    INDEX idx_material (material_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: borrow (Préstamos de materiales)
CREATE TABLE IF NOT EXISTS borrow (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    resource_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status ENUM(
        'PENDING',
        'BORROWED',
        'RETURNED',
        'OVERDUE',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES materials (id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_resource (resource_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    INDEX idx_return_date (return_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: details_borrow (Detalles de los préstamos)
CREATE TABLE IF NOT EXISTS details_borrow (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    borrow_id INT NOT NULL,
    material_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (borrow_id) REFERENCES borrow (id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE RESTRICT,
    INDEX idx_borrow (borrow_id),
    INDEX idx_material (material_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: movements (Historial de movimientos de materiales)
CREATE TABLE IF NOT EXISTS movements (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resource_id INT NOT NULL,
    user_id INT NOT NULL,
    move_type ENUM(
        'BORROW',
        'RETURN',
        'PURCHASE',
        'DONATION',
        'ADJUSTMENT'
    ) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES materials (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_resource (resource_id),
    INDEX idx_user (user_id),
    INDEX idx_move_type (move_type),
    INDEX idx_movement_date (movement_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- PASO 3: Insertar datos iniciales de ejemplo

-- Insertar roles con IDs fijos (asegura compatibilidad con código que busca por id)
INSERT INTO
    roles (
        id,
        name,
        description,
        created_at
    )
VALUES (
        1,
        'ADMIN',
        'Administrador del sistema',
        NOW()
    ),
    (
        2,
        'USER',
        'Usuario regular',
        NOW()
    )
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description);

-- Insertar usuario admin por defecto para pruebas
-- Email: jafethgamboabaas@gmail.com
-- Password: Jafeth_270703
-- Hash BCrypt generado con BCryptPasswordEncoder (strength 10)
INSERT INTO
    users (
        username,
        email,
        password,
        first_name,
        last_name,
        is_enabled
    )
SELECT 'jafethgamboabaas', 'jafethgamboabaas@gmail.com', '$2a$10$G2QZUq7/UBQFAJu/ynIiWeYUqRkxYGqWG5xXvPelgeywvOqCM/k9q', 'Jafeth Daniel', 'Gamboa Baas', TRUE
FROM DUAL
WHERE
    NOT EXISTS (
        SELECT 1
        FROM users
        WHERE
            email = 'jafethgamboabaas@gmail.com'
    );

-- Asignar rol ADMIN al usuario por defecto para pruebas
INSERT INTO
    user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
    JOIN roles r ON r.name = 'ADMIN'
WHERE
    u.email = 'jafethgamboabaas@gmail.com'
    AND NOT EXISTS (
        SELECT 1
        FROM user_role ur
        WHERE
            ur.user_id = u.id
            AND ur.role_id = r.id
    );

-- Categorías iniciales
INSERT INTO
    categories (name, description)
VALUES (
        'Electrónica',
        'Componentes y dispositivos electrónicos'
    ),
    (
        'Hardware',
        'Componentes de computadora'
    ),
    (
        'Accesorios',
        'Accesorios varios'
    ),
    (
        'Software',
        'Licencias y software'
    );

-- Subcategorías iniciales
INSERT INTO
    sub_categories (
        category_id,
        name,
        description
    )
VALUES (
        1,
        'Microcontroladores',
        'Arduino, ESP32, STM32'
    ),
    (
        1,
        'Sensores',
        'Sensores de temperatura, humedad, distancia'
    ),
    (
        2,
        'Procesadores',
        'CPUs y GPUs'
    ),
    (2, 'Memoria', 'RAM, SSD, HDD'),
    (
        3,
        'Cables',
        'Cables USB, HDMI, Ethernet'
    ),
    (
        3,
        'Conectores',
        'Conectores y adaptadores'
    );

-- NOTA IMPORTANTE: Si se agregan inserts en 'materials', incluir siempre el campo sub_category_id y ponerlo como NULL si no aplica.
-- Ejemplo correcto:
-- INSERT INTO materials (name, description, price, stock, sub_category_id, image_path, created_by) VALUES ('Material X', 'Desc', 10.0, 5, NULL, 'img.png', 1);

-- Tabla: borrow (Préstamos de materiales)
CREATE TABLE IF NOT EXISTS borrow (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    resource_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status ENUM(
        'PENDING',
        'BORROWED',
        'RETURNED',
        'OVERDUE',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES materials (id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_resource (resource_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    INDEX idx_return_date (return_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: details_borrow (Detalles de los préstamos)
CREATE TABLE IF NOT EXISTS details_borrow (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    borrow_id INT NOT NULL,
    material_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (borrow_id) REFERENCES borrow (id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials (id) ON DELETE RESTRICT,
    INDEX idx_borrow (borrow_id),
    INDEX idx_material (material_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: movements (Historial de movimientos de materiales)
CREATE TABLE IF NOT EXISTS movements (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resource_id INT NOT NULL,
    user_id INT NOT NULL,
    move_type ENUM(
        'BORROW',
        'RETURN',
        'PURCHASE',
        'DONATION',
        'ADJUSTMENT'
    ) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES materials (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_resource (resource_id),
    INDEX idx_user (user_id),
    INDEX idx_move_type (move_type),
    INDEX idx_movement_date (movement_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tabla: verification_token (para verificación de email)
CREATE TABLE IF NOT EXISTS verification_token (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user (user_id),
    INDEX idx_expiry_date (expiry_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- PASO 4: Verificaciones finales
-- Asegurar que todo se creó correctamente en snake_case
-- Esto se valida automáticamente por Hibernate en el siguiente deploy