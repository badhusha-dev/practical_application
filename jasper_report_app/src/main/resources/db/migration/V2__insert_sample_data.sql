-- Insert sample users
INSERT INTO users (name, email) VALUES 
('John Doe', 'john.doe@example.com'),
('Jane Smith', 'jane.smith@example.com'),
('Bob Johnson', 'bob.johnson@example.com'),
('Alice Brown', 'alice.brown@example.com'),
('Charlie Wilson', 'charlie.wilson@example.com');

-- Insert sample products
INSERT INTO products (name, price, description) VALUES 
('Laptop', 999.99, 'High-performance laptop'),
('Mouse', 29.99, 'Wireless optical mouse'),
('Keyboard', 79.99, 'Mechanical gaming keyboard'),
('Monitor', 299.99, '27-inch 4K monitor'),
('Headphones', 149.99, 'Noise-cancelling headphones'),
('Webcam', 89.99, 'HD webcam for video calls'),
('Tablet', 399.99, '10-inch Android tablet'),
('Smartphone', 699.99, 'Latest smartphone model');

-- Insert sample orders
INSERT INTO orders (user_id, product_id, quantity, unit_price, total_amount, order_date) VALUES 
(1, 1, 1, 999.99, 999.99, '2024-01-15 10:30:00'),
(1, 2, 2, 29.99, 59.98, '2024-01-15 10:30:00'),
(2, 3, 1, 79.99, 79.99, '2024-01-16 14:20:00'),
(2, 4, 1, 299.99, 299.99, '2024-01-16 14:20:00'),
(3, 5, 1, 149.99, 149.99, '2024-01-17 09:15:00'),
(3, 6, 1, 89.99, 89.99, '2024-01-17 09:15:00'),
(4, 7, 1, 399.99, 399.99, '2024-01-18 16:45:00'),
(4, 8, 1, 699.99, 699.99, '2024-01-18 16:45:00'),
(5, 1, 1, 999.99, 999.99, '2024-01-19 11:30:00'),
(5, 3, 1, 79.99, 79.99, '2024-01-19 11:30:00'),
(1, 4, 1, 299.99, 299.99, '2024-01-20 13:20:00'),
(2, 5, 2, 149.99, 299.98, '2024-01-21 15:10:00'),
(3, 6, 1, 89.99, 89.99, '2024-01-22 08:45:00'),
(4, 7, 1, 399.99, 399.99, '2024-01-23 12:30:00'),
(5, 8, 1, 699.99, 699.99, '2024-01-24 17:15:00');

