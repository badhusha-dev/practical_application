-- V2__Insert_sample_data.sql
-- Insert sample users
INSERT INTO users (username, password) VALUES 
('admin', 'admin123'),
('user1', 'password123'),
('user2', 'password456'),
('testuser', 'testpass');

-- Insert sample products
INSERT INTO products (name, price) VALUES 
('Laptop', 999.99),
('Smartphone', 699.99),
('Tablet', 399.99),
('Headphones', 199.99),
('Keyboard', 79.99),
('Mouse', 49.99),
('Monitor', 299.99),
('Webcam', 129.99),
('Speaker', 149.99),
('Charger', 29.99),
('USB Cable', 19.99),
('Laptop Stand', 89.99),
('Wireless Router', 159.99),
('External Hard Drive', 199.99),
('Gaming Mouse', 89.99);
