# Business Collections Demo

A Spring Boot application demonstrating Java Collections in a real business context. This project showcases how different Java Collections (List, Set, Map, Queue, Stack) are used in enterprise applications for managing employees, departments, and tasks.

## 🎯 Project Overview

This application simulates a small company's management system where Java Collections are used to handle:
- **List<Employee>** - Store and iterate through employees
- **Set<String>** - Maintain unique employee skills
- **Map<Integer, Department>** - Fast department lookups by ID
- **Queue<Task>** - Manage pending tasks (FIFO)
- **Stack<Task>** - Track completed tasks (LIFO)
- **HashMap<Integer, List<Task>>** - Assign tasks to employees

## 🏗️ Architecture

```
com.example.collections/
├── controller/
│   └── CompanyController.java     # REST + Thymeleaf endpoints
├── entity/
│   ├── Employee.java              # Employee entity
│   ├── Department.java           # Department entity
│   └── Task.java                 # Task entity
├── repository/
│   ├── EmployeeRepository.java    # JPA repository
│   ├── DepartmentRepository.java # JPA repository
│   └── TaskRepository.java       # JPA repository
├── service/
│   └── CompanyService.java       # Business logic with collections
└── BusinessCollectionsDemoApplication.java
```

## 🚀 Features

### Java Collections Demonstration

1. **List<Employee>**
   - Iteration through employees
   - Filtering by department
   - Stream operations for data extraction

2. **Set<String>**
   - Unique skills management
   - No duplicate skills across organization
   - Set operations demonstration

3. **Map<Integer, Department>**
   - Fast department lookups by ID
   - Key-value pair management
   - Map iteration and operations

4. **Queue<Task>**
   - FIFO (First In, First Out) task management
   - Pending tasks queue
   - offer(), poll(), peek() operations

5. **Stack<Task>**
   - LIFO (Last In, First Out) task history
   - Completed tasks tracking
   - push(), pop(), peek() operations

6. **HashMap<Integer, List<Task>>**
   - Employee task assignments
   - Complex map operations
   - Nested collection management

### Web Interface

- **Home Page** (`/`) - Overview of collections
- **Employees** (`/employees`) - List view with department mapping
- **Departments** (`/departments`) - Department cards with Map operations
- **Tasks** (`/tasks`) - Queue and Stack visualization with interactive features

### REST API

- `GET /api/employees` - Get all employees
- `GET /api/departments` - Get all departments
- `GET /api/skills` - Get unique skills
- `GET /api/tasks/queue` - Get pending tasks
- `GET /api/tasks/history` - Get completed tasks
- `POST /api/tasks/complete` - Complete next task
- `POST /api/tasks` - Add new task

## 🛠️ Technology Stack

- **Spring Boot 3.2.0** - Main framework
- **Spring Data JPA** - Database operations
- **Spring Web** - REST endpoints and Thymeleaf
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **Thymeleaf** - Template engine
- **Bootstrap 5** - UI framework

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL database
- Maven 3.6+

## 🚀 Setup Instructions

### 1. Database Setup

Create a PostgreSQL database:
```sql
CREATE DATABASE collections_demo;
```

### 2. Configuration

Update `src/main/resources/application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/collections_demo
    username: your_username
    password: your_password
```

### 3. Run the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Database Migration

Flyway will automatically run migrations:
- `V1__Create_tables.sql` - Creates tables
- `V2__Insert_sample_data.sql` - Inserts sample data

## 📊 Sample Data

The application comes with sample data:

**Departments:**
- IT (ID: 1)
- HR (ID: 2)
- Finance (ID: 3)

**Employees:**
- Alice Johnson (IT) - Java, Spring, Database
- Bob Smith (HR) - Recruitment, Onboarding, Communication
- Charlie Brown (Finance) - Accounting, Excel, Financial Analysis
- Diana Prince (IT) - JavaScript, React, UI/UX
- Edward Norton (HR) - Employee Relations, Training, Compliance

**Tasks:**
- Implement user authentication system (Priority: 1)
- Review quarterly financial reports (Priority: 2)
- Conduct employee performance reviews (Priority: 3)
- Update company website (Priority: 1)
- Process payroll for current month (Priority: 2)
- Organize team building event (Priority: 4)

## 🎮 Usage

### Web Interface

1. **Home Page** - Overview of all collections
2. **Employees Page** - View employee list with department mapping
3. **Departments Page** - See department cards with Map operations
4. **Tasks Page** - Interactive task management with Queue/Stack

### API Testing

Use tools like Postman or curl:

```bash
# Get all employees
curl http://localhost:8080/api/employees

# Get unique skills
curl http://localhost:8080/api/skills

# Complete next task
curl -X POST http://localhost:8080/api/tasks/complete

# Add new task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"description":"New task","priority":2}'
```

## 📝 Console Logging

The application provides detailed console logs showing:
- Collection initialization
- List operations (iteration, filtering)
- Set operations (unique skills)
- Map operations (lookups, iteration)
- Queue operations (FIFO)
- Stack operations (LIFO)

## 🔧 Customization

### Adding New Collections

1. Add new collection fields in `CompanyService.java`
2. Implement initialization logic
3. Add getter methods
4. Create corresponding API endpoints
5. Update Thymeleaf templates

### Extending Business Logic

1. Add new methods in `CompanyService.java`
2. Create corresponding controller endpoints
3. Update templates as needed

## 📚 Learning Objectives

This project demonstrates:

1. **Collection Types** - When to use List, Set, Map, Queue, Stack
2. **Performance** - O(1) vs O(n) operations
3. **Business Logic** - Real-world application of collections
4. **Spring Boot Integration** - Collections in enterprise applications
5. **Database Integration** - Persistence with collections
6. **Web Interface** - Visual representation of collection operations

## 🤝 Contributing

Feel free to extend this project with:
- Additional collection types
- More complex business logic
- Enhanced UI features
- Additional API endpoints

## 📄 License

This project is for educational purposes demonstrating Java Collections in Spring Boot applications.
