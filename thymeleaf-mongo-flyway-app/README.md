# Thymeleaf MongoDB Flyway App

A complete production-ready Spring Boot application demonstrating modern web development with Thymeleaf, MongoDB, and Mongock migrations.

## 🚀 Features

- **Spring Boot 3.2.0** with Java 17
- **MongoDB** as the primary database
- **Thymeleaf** for server-side rendering
- **Mongock** for database migrations
- **Bootstrap 5 + Tailwind CSS** for modern UI
- **Responsive Design** with mobile-first approach
- **Form Validation** with Hibernate Validator
- **CRUD Operations** for User management
- **Search & Pagination** functionality
- **Modern Dashboard** with SB Admin style

## 🛠️ Tech Stack

- **Backend**: Spring Boot, Spring Data MongoDB, Spring Web MVC
- **Frontend**: Thymeleaf, Bootstrap 5, Tailwind CSS, Bootstrap Icons
- **Database**: MongoDB
- **Migrations**: Mongock
- **Build Tool**: Maven
- **Validation**: Hibernate Validator
- **Utilities**: Lombok

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB 4.4+

## 🚀 Quick Start

### 1. Clone and Setup

```bash
# Navigate to project directory
cd thymeleaf-mongo-flyway-app

# Install dependencies
mvn clean install
```

### 2. Database Setup

```bash
# Start MongoDB (if not running)
mongod

# Create database (optional - will be created automatically)
mongo
> use thymeleafdb
```

### 3. Configuration

Update `src/main/resources/application.yml` if needed:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/thymeleafdb

mongock:
  change-logs-scan-package: com.example.thymeleafmongoflywayapp.migration
```

### 4. Run Application

```bash
# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/thymeleaf-mongo-flyway-app-0.0.1-SNAPSHOT.jar
```

### 5. Access Application

- **Main Application**: http://localhost:8080
- **Dashboard**: http://localhost:8080/dashboard
- **Users List**: http://localhost:8080/users

## 📁 Project Structure

```
thymeleaf-mongo-flyway-app/
├── src/main/java/com/example/thymeleafmongoflywayapp/
│   ├── config/
│   │   └── MongockConfig.java              # Mongock configuration
│   ├── controller/
│   │   ├── HomeController.java             # Dashboard controller
│   │   └── UserController.java             # User CRUD controller
│   ├── entity/
│   │   └── User.java                       # User entity with MongoDB annotations
│   ├── migration/
│   │   └── CreateUsersCollectionMigration.java  # Database migration
│   ├── repository/
│   │   └── UserRepository.java             # MongoDB repository
│   ├── service/
│   │   └── UserService.java                # Business logic service
│   └── ThymeleafMongoFlywayAppApplication.java  # Main application class
├── src/main/resources/
│   ├── static/
│   │   ├── css/
│   │   │   └── style.css                   # Custom CSS styles
│   │   └── js/
│   │       └── app.js                      # Custom JavaScript
│   ├── templates/
│   │   ├── dashboard.html                  # Dashboard page
│   │   ├── layout.html                     # Base layout template
│   │   └── users/
│   │       ├── form.html                   # Add/Edit user form
│   │       ├── list.html                   # Users list page
│   │       └── view.html                   # User details page
│   └── application.yml                     # Application configuration
└── pom.xml                                 # Maven dependencies
```

## 🎯 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Dashboard home |
| GET | `/dashboard` | Dashboard with stats |
| GET | `/users` | List users with pagination/search |
| GET | `/users/new` | Show add user form |
| POST | `/users/save` | Save new user |
| GET | `/users/edit/{id}` | Show edit user form |
| POST | `/users/update/{id}` | Update existing user |
| GET | `/users/delete/{id}` | Delete user |
| GET | `/users/view/{id}` | View user details |

## 🎨 UI Features

### Dashboard
- **Stats Cards**: Total users, active users, system status
- **Quick Actions**: Navigate to users, add new user
- **System Info**: Database connection status
- **Responsive Design**: Mobile-friendly layout

### Users Management
- **Search & Filter**: Search by name or email
- **Sorting**: Sort by name, email, age, or creation date
- **Pagination**: Navigate through large datasets
- **CRUD Operations**: Create, read, update, delete users
- **Form Validation**: Client and server-side validation
- **Responsive Table**: Mobile-optimized user list

### Modern UI Elements
- **Bootstrap 5**: Latest Bootstrap framework
- **Tailwind CSS**: Utility-first CSS framework
- **Bootstrap Icons**: Comprehensive icon set
- **Custom Animations**: Smooth transitions and effects
- **Dark/Light Theme**: Professional color scheme
- **Mobile Responsive**: Works on all devices

## 🗄️ Database Schema

### Users Collection
```javascript
{
  "_id": ObjectId,
  "name": String (required),
  "email": String (required, unique),
  "age": Number (required, min: 1),
  "created_at": Date,
  "updated_at": Date
}
```

### Indexes
- **email**: Unique index for email field
- **name**: Index for name field (search optimization)
- **age**: Index for age field (filtering)
- **created_at**: Index for creation date (sorting)

## 🔧 Configuration Options

### Application Properties
```yaml
# Server Configuration
server:
  port: 8080

# MongoDB Configuration
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/thymeleafdb
      auto-index-creation: true

# Mongock Configuration
mongock:
  change-logs-scan-package: com.example.thymeleafmongoflywayapp.migration

# Thymeleaf Configuration
spring:
  thymeleaf:
    cache: false
    prefix: classpath:/templates/
    suffix: .html
    encoding: UTF-8
    mode: HTML

# Logging Configuration
logging:
  level:
    com.example.thymeleafmongoflywayapp: DEBUG
    org.springframework.data.mongodb.core.MongoTemplate: DEBUG
```

## 🧪 Testing

### Manual Testing
1. **Create User**: Navigate to `/users/new` and fill the form
2. **Search Users**: Use the search box to find users
3. **Edit User**: Click edit button and modify user details
4. **Delete User**: Click delete button and confirm
5. **Pagination**: Navigate through multiple pages
6. **Sorting**: Click column headers to sort data

### Automated Testing
```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/thymeleaf-mongo-flyway-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Configuration
```yaml
# Production settings
spring:
  profiles:
    active: prod
  
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/thymeleafdb}

logging:
  level:
    com.example.thymeleafmongoflywayapp: INFO
    org.springframework.data.mongodb.core.MongoTemplate: WARN
```

## 📊 Performance Features

- **Database Indexing**: Optimized queries with proper indexes
- **Pagination**: Efficient handling of large datasets
- **Caching**: Thymeleaf template caching
- **Lazy Loading**: Optimized data loading
- **Connection Pooling**: MongoDB connection optimization

## 🔒 Security Features

- **Input Validation**: Server-side validation with Hibernate Validator
- **XSS Protection**: Thymeleaf automatic XSS protection
- **CSRF Protection**: Spring Security CSRF tokens
- **SQL Injection Prevention**: MongoDB parameterized queries

## 🎯 Best Practices Demonstrated

1. **Clean Architecture**: Separation of concerns with layered architecture
2. **RESTful Design**: Proper HTTP methods and status codes
3. **Error Handling**: Comprehensive error handling and user feedback
4. **Responsive Design**: Mobile-first responsive design
5. **Performance Optimization**: Database indexing and query optimization
6. **Code Quality**: Lombok for boilerplate reduction
7. **Documentation**: Comprehensive code documentation
8. **Testing**: Unit and integration testing strategies

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the code examples

## 🔄 Version History

- **v1.0.0**: Initial release with basic CRUD operations
- **v1.1.0**: Added search and pagination
- **v1.2.0**: Enhanced UI with Bootstrap 5 and Tailwind CSS
- **v1.3.0**: Added Mongock migrations and seed data

---

**Built with ❤️ using Spring Boot, MongoDB, and Thymeleaf**
