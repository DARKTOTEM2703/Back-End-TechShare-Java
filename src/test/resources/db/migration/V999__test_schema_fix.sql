-- Esquema mínimo para pruebas H2 (evita errores de tablas faltantes)
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100),
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  email VARCHAR(150),
  password VARCHAR(255),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS materials (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200),
  description VARCHAR(1000),
  price DECIMAL(10,2),
  stock INT,
  borrowable_stock INT
);

CREATE TABLE IF NOT EXISTS borrow (
  id INT PRIMARY KEY AUTO_INCREMENT,
  borrow_date DATE,
  due_date DATE,
  return_date DATE,
  status VARCHAR(50),
  resource_id INT,
  amount DECIMAL(10,2),
  user_id INT
);

CREATE TABLE IF NOT EXISTS details_borrow (
  id INT PRIMARY KEY AUTO_INCREMENT,
  quantity INT,
  unit_price DECIMAL(10,2),
  total_price DECIMAL(10,2),
  material_id INT,
  borrow_id INT
);

CREATE TABLE IF NOT EXISTS movements (
  id INT PRIMARY KEY AUTO_INCREMENT,
  movement_type VARCHAR(50),
  resource_id INT,
  notes VARCHAR(1000),
  movement_date TIMESTAMP,
  user_id INT
);
