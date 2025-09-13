INSERT INTO departments (name) VALUES 
    ('IT'), 
    ('HR'), 
    ('Finance');

INSERT INTO employees (name, department_id, skills) VALUES
    ('Alice Johnson', 1, 'Java,Spring,Database'),
    ('Bob Smith', 2, 'Recruitment,Onboarding,Communication'),
    ('Charlie Brown', 3, 'Accounting,Excel,Financial Analysis'),
    ('Diana Prince', 1, 'JavaScript,React,UI/UX'),
    ('Edward Norton', 2, 'Employee Relations,Training,Compliance');

INSERT INTO tasks (description, priority) VALUES
    ('Implement user authentication system', 1),
    ('Review quarterly financial reports', 2),
    ('Conduct employee performance reviews', 3),
    ('Update company website', 1),
    ('Process payroll for current month', 2),
    ('Organize team building event', 4);
