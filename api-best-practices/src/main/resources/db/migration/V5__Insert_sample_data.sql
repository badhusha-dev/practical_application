-- V5__Insert_sample_data.sql
-- Insert sample users
INSERT INTO users (username, email, first_name, last_name, role) VALUES
('admin', 'admin@example.com', 'Admin', 'User', 'ADMIN'),
('john.doe', 'john.doe@example.com', 'John', 'Doe', 'USER'),
('jane.smith', 'jane.smith@example.com', 'Jane', 'Smith', 'USER'),
('manager1', 'manager1@example.com', 'Manager', 'One', 'MANAGER');

-- Insert sample orders
INSERT INTO orders (order_number, user_id, status, total_amount, currency, shipping_address) VALUES
('ORD-001', 2, 'CONFIRMED', 99.99, 'USD', '123 Main St, Anytown, USA'),
('ORD-002', 3, 'SHIPPED', 149.50, 'USD', '456 Oak Ave, Somewhere, USA'),
('ORD-003', 2, 'DELIVERED', 75.25, 'USD', '789 Pine Rd, Elsewhere, USA');

-- Insert sample order items
INSERT INTO order_items (order_id, product_name, product_sku, quantity, unit_price, total_price) VALUES
(1, 'Wireless Headphones', 'WH-001', 1, 99.99, 99.99),
(2, 'Smart Watch', 'SW-002', 1, 149.50, 149.50),
(3, 'USB Cable', 'UC-003', 3, 25.08, 75.25);
