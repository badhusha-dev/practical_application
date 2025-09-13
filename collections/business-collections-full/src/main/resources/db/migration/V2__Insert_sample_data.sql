-- V2__Insert_sample_data.sql
INSERT INTO departments (name) VALUES 
    ('IT'), 
    ('HR'), 
    ('Finance'),
    ('Marketing'),
    ('Operations');

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
    ('Jack Taylor', 4, 'Social Media,Brand Management');

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
    ('Update user training materials', 2, 'COMPLETED');
