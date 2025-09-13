-- V2__Insert_sample_data.sql
-- Insert sample categories
INSERT INTO categories (name) VALUES 
('Electronics'),
('Clothing'),
('Books'),
('Home & Garden'),
('Sports'),
('Toys'),
('Automotive'),
('Health & Beauty');

-- Insert sample users
INSERT INTO users (username, email, password) VALUES 
('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'), -- password: admin123
('user1', 'user1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'), -- password: admin123
('user2', 'user2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'), -- password: admin123
('testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'); -- password: admin123

-- Insert sample products
INSERT INTO products (name, price, category_id) VALUES 
-- Electronics
('Laptop', 999.99, 1),
('Smartphone', 699.99, 1),
('Tablet', 399.99, 1),
('Headphones', 199.99, 1),
('Keyboard', 79.99, 1),
('Mouse', 49.99, 1),
('Monitor', 299.99, 1),
('Webcam', 129.99, 1),
('Speaker', 149.99, 1),
('Charger', 29.99, 1),

-- Clothing
('T-Shirt', 19.99, 2),
('Jeans', 59.99, 2),
('Sneakers', 89.99, 2),
('Jacket', 129.99, 2),
('Dress', 79.99, 2),
('Hat', 24.99, 2),
('Socks', 9.99, 2),
('Belt', 29.99, 2),

-- Books
('Programming Book', 49.99, 3),
('Novel', 14.99, 3),
('Cookbook', 24.99, 3),
('Textbook', 89.99, 3),
('Magazine', 5.99, 3),
('Comic Book', 12.99, 3),

-- Home & Garden
('Coffee Maker', 89.99, 4),
('Vacuum Cleaner', 199.99, 4),
('Garden Tools', 39.99, 4),
('Plant Pot', 19.99, 4),
('Lamp', 49.99, 4),
('Cushion', 24.99, 4),

-- Sports
('Tennis Racket', 79.99, 5),
('Basketball', 29.99, 5),
('Running Shoes', 99.99, 5),
('Yoga Mat', 34.99, 5),
('Dumbbells', 59.99, 5),
('Bicycle', 299.99, 5),

-- Toys
('Action Figure', 19.99, 6),
('Board Game', 39.99, 6),
('Puzzle', 24.99, 6),
('Doll', 29.99, 6),
('Building Blocks', 49.99, 6),
('Remote Control Car', 79.99, 6),

-- Automotive
('Car Battery', 149.99, 7),
('Oil Filter', 19.99, 7),
('Tire', 89.99, 7),
('Brake Pads', 59.99, 7),
('Air Filter', 24.99, 7),
('Spark Plugs', 34.99, 7),

-- Health & Beauty
('Shampoo', 12.99, 8),
('Face Cream', 29.99, 8),
('Toothbrush', 8.99, 8),
('Vitamins', 19.99, 8),
('Perfume', 49.99, 8),
('Makeup Kit', 39.99, 8);
