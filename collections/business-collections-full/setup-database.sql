-- Manual PostgreSQL Setup Script
-- Run this in pgAdmin or any PostgreSQL client

-- 1. Create database
CREATE DATABASE business_collections;

-- 2. Connect to the new database and create user (if needed)
-- \c business_collections;

-- 3. Create user with password (optional - you can use postgres user)
-- CREATE USER business_user WITH PASSWORD 'badsha@123';
-- GRANT ALL PRIVILEGES ON DATABASE business_collections TO business_user;

-- 4. Reset postgres user password
ALTER USER postgres PASSWORD 'badsha@123';

-- 5. Grant all privileges
GRANT ALL PRIVILEGES ON DATABASE business_collections TO postgres;

-- 6. Create tables manually (if Flyway fails)
CREATE TABLE IF NOT EXISTS departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT REFERENCES departments(id),
    skills TEXT
);

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    priority INT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING'
);

-- 7. Insert sample data
INSERT INTO departments (name) VALUES 
    ('IT'), 
    ('HR'), 
    ('Finance'),
    ('Marketing'),
    ('Operations')
ON CONFLICT (name) DO NOTHING;

INSERT INTO employees (name, department_id, skills) VALUES
    ('Alice Johnson', 1, 'Java,Spring,React,Angular'),
    ('Bob Smith', 2, 'Recruitment,Onboarding,Training'),
    ('Charlie Brown', 3, 'Accounting,Excel,Financial Analysis'),
    ('Diana Prince', 1, 'Python,Docker,Kubernetes'),
    ('Eve Wilson', 4, 'Digital Marketing,SEO,Content Creation'),
    ('Frank Miller', 5, 'Project Management,Process Improvement'),
    ('Grace Lee', 1, 'JavaScript,Node.js,MongoDB'),
    ('Henry Davis', 2, 'Employee Relations,Performance Management'),
    ('Ivy Chen', 3, 'Tax Planning,Budgeting,Auditing'),
    ('Jack Taylor', 4, 'Social Media,Brand Management')
ON CONFLICT DO NOTHING;

INSERT INTO tasks (description, priority, status) VALUES
    ('Prepare quarterly financial report', 3, 'PENDING'),
    ('Send email notification to all employees', 1, 'PENDING'),
    ('Database migration to new server', 3, 'PENDING'),
    ('Update employee handbook', 2, 'PENDING'),
    ('Review and approve budget proposals', 2, 'PENDING'),
    ('Conduct performance reviews', 2, 'PENDING'),
    ('Implement new security protocols', 3, 'PENDING'),
    ('Organize team building event', 1, 'PENDING'),
    ('Update company website', 2, 'PENDING'),
    ('Prepare presentation for board meeting', 3, 'PENDING'),
    ('Complete project documentation', 1, 'COMPLETED'),
    ('Fix critical bug in production', 3, 'COMPLETED'),
    ('Update user training materials', 2, 'COMPLETED')
ON CONFLICT DO NOTHING;
