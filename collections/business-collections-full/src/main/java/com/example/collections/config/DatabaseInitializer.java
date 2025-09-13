package com.example.collections.config;

import com.example.collections.entity.Department;
import com.example.collections.entity.Employee;
import com.example.collections.entity.Task;
import com.example.collections.repository.DepartmentRepository;
import com.example.collections.repository.EmployeeRepository;
import com.example.collections.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private DatabaseConnectionTest connectionTest;

    @Override
    public void run(String... args) throws Exception {
        // Test connection first
        connectionTest.testConnection();
        
        System.out.println("=== Initializing Database with Sample Data ===");
        
        // Only initialize if no data exists
        if (departmentRepository.count() == 0) {
            initializeData();
        } else {
            System.out.println("Database already contains data, skipping initialization.");
        }
    }
    
    private void initializeData() {
        // Create departments
        Department itDept = new Department("IT");
        Department hrDept = new Department("HR");
        Department financeDept = new Department("Finance");
        Department marketingDept = new Department("Marketing");
        Department operationsDept = new Department("Operations");
        
        itDept = departmentRepository.save(itDept);
        hrDept = departmentRepository.save(hrDept);
        financeDept = departmentRepository.save(financeDept);
        marketingDept = departmentRepository.save(marketingDept);
        operationsDept = departmentRepository.save(operationsDept);
        
        System.out.println("Created " + departmentRepository.count() + " departments");
        
        // Create employees
        Employee emp1 = new Employee("Alice Johnson", itDept.getId(), "Java,Spring,React,Angular");
        Employee emp2 = new Employee("Bob Smith", hrDept.getId(), "Recruitment,Onboarding,Training");
        Employee emp3 = new Employee("Charlie Brown", financeDept.getId(), "Accounting,Excel,Financial Analysis");
        Employee emp4 = new Employee("Diana Prince", itDept.getId(), "Python,Docker,Kubernetes");
        Employee emp5 = new Employee("Eve Wilson", marketingDept.getId(), "Digital Marketing,SEO,Content Creation");
        Employee emp6 = new Employee("Frank Miller", operationsDept.getId(), "Project Management,Process Improvement");
        Employee emp7 = new Employee("Grace Lee", itDept.getId(), "JavaScript,Node.js,MongoDB");
        Employee emp8 = new Employee("Henry Davis", hrDept.getId(), "Employee Relations,Performance Management");
        Employee emp9 = new Employee("Ivy Chen", financeDept.getId(), "Tax Planning,Budgeting,Auditing");
        Employee emp10 = new Employee("Jack Taylor", marketingDept.getId(), "Social Media,Brand Management");
        
        employeeRepository.save(emp1);
        employeeRepository.save(emp2);
        employeeRepository.save(emp3);
        employeeRepository.save(emp4);
        employeeRepository.save(emp5);
        employeeRepository.save(emp6);
        employeeRepository.save(emp7);
        employeeRepository.save(emp8);
        employeeRepository.save(emp9);
        employeeRepository.save(emp10);
        
        System.out.println("Created " + employeeRepository.count() + " employees");
        
        // Create tasks
        Task task1 = new Task("Prepare quarterly financial report", 3, "PENDING");
        Task task2 = new Task("Send email notification to all employees", 1, "PENDING");
        Task task3 = new Task("Database migration to new server", 3, "PENDING");
        Task task4 = new Task("Update employee handbook", 2, "PENDING");
        Task task5 = new Task("Review and approve budget proposals", 2, "PENDING");
        Task task6 = new Task("Conduct performance reviews", 2, "PENDING");
        Task task7 = new Task("Implement new security protocols", 3, "PENDING");
        Task task8 = new Task("Organize team building event", 1, "PENDING");
        Task task9 = new Task("Update company website", 2, "PENDING");
        Task task10 = new Task("Prepare presentation for board meeting", 3, "PENDING");
        Task task11 = new Task("Complete project documentation", 1, "COMPLETED");
        Task task12 = new Task("Fix critical bug in production", 3, "COMPLETED");
        Task task13 = new Task("Update user training materials", 2, "COMPLETED");
        
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        taskRepository.save(task5);
        taskRepository.save(task6);
        taskRepository.save(task7);
        taskRepository.save(task8);
        taskRepository.save(task9);
        taskRepository.save(task10);
        taskRepository.save(task11);
        taskRepository.save(task12);
        taskRepository.save(task13);
        
        System.out.println("Created " + taskRepository.count() + " tasks");
        System.out.println("=== Database Initialization Complete ===");
    }
}
