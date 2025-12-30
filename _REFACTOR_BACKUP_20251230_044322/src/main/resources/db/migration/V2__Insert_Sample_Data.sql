-- ============================================
-- V2: Insert sample materials data
-- ============================================

-- Insert materials for testing
INSERT INTO materials (name, description, price, stock, sub_category_id, created_by)
VALUES 
    ('Arduino Uno', 'Placa microcontroladora Arduino Uno R3 con ATmega328P', 25.99, 15, 1, 1),
    ('ESP32 DevKit', 'Módulo ESP32 con WiFi y Bluetooth integrado', 19.99, 20, 1, 1),
    ('Sensor DHT22', 'Sensor de temperatura y humedad digital', 12.50, 30, 2, 1),
    ('Intel i7-13700K', 'Procesador Intel Core i7 de 13va generación', 429.99, 5, 3, 1),
    ('Kingston DDR5 16GB', 'Memoria RAM DDR5 16GB 5600MHz', 89.99, 10, 4, 1),
    ('SSD Samsung 970 EVO', 'Disco SSD M.2 NVMe 1TB', 129.99, 8, 4, 1),
    ('Cable USB-C 3.1', 'Cable USB Type-C 3.1 SuperSpeed 2 metros', 8.99, 50, 5, 1),
    ('Adaptador HDMI', 'Adaptador HDMI 2.1 8K premium', 15.99, 25, 6, 1),
    ('Raspberry Pi 4 8GB', 'Mini computadora Raspberry Pi 4 con 8GB RAM', 75.00, 12, 1, 1),
    ('Sensor ultrasónico', 'Sensor de distancia ultrasónico HC-SR04', 6.99, 40, 2, 1);

-- Insert sample borrow requests
INSERT INTO borrow (user_id, resource_id, borrow_date, due_date, status, amount)
VALUES 
    (1, 1, '2025-12-10', '2025-12-24', 'PENDING', 77.97),
    (1, 3, '2025-12-08', '2025-12-22', 'BORROWED', 25.00),
    (1, 5, '2025-12-05', '2025-12-19', 'RETURNED', 89.99);

-- Insert details for borrow requests
INSERT INTO details_borrow (borrow_id, material_id, quantity, unit_price, total_price)
VALUES 
    (1, 1, 2, 25.99, 51.98),
    (1, 2, 1, 19.99, 19.99),
    (1, 3, 1, 12.50, 12.50),
    (2, 3, 2, 12.50, 25.00),
    (3, 5, 1, 89.99, 89.99);
