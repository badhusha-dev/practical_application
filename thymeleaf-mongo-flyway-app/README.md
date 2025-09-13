# 🚀 Thymeleaf MongoDB Flyway App

A complete **production-ready Spring Boot application** demonstrating modern web development with Thymeleaf, MongoDB, and Mongock migrations. This project showcases best practices for building scalable web applications with a modern tech stack.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![MongoDB](https://img.shields.io/badge/MongoDB-4.4+-green)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1+-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-purple)
![License](https://img.shields.io/badge/License-MIT-yellow)

## ✨ Key Features

- 🎯 **Spring Boot 3.2.0** with Java 17
- 🗄️ **MongoDB** as the primary database with Mongock migrations
- 🎨 **Thymeleaf** for server-side rendering
- 💎 **Bootstrap 5 + Tailwind CSS** for modern, responsive UI
- 📱 **Mobile-First Design** with responsive layout
- ✅ **Form Validation** with Hibernate Validator
- 🔍 **Advanced Search & Pagination** functionality
- 📊 **Modern Dashboard** with SB Admin style
- 🚀 **Production-Ready** with comprehensive error handling
- 🔒 **Security Features** with input validation and XSS protection

## 🛠️ Tech Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Backend** | Spring Boot | 3.2.0 | Application framework |
| | Spring Data MongoDB | 3.2.0 | Database operations |
| | Spring Web MVC | 3.2.0 | Web layer |
| **Frontend** | Thymeleaf | 3.1+ | Server-side templating |
| | Bootstrap 5 | 5.3.0 | CSS framework |
| | Tailwind CSS | Latest | Utility-first CSS |
| | Bootstrap Icons | 1.10.0 | Icon library |
| **Database** | MongoDB | 4.4+ | NoSQL database |
| **Migrations** | Mongock | 5.3.2 | Database versioning |
| **Build Tool** | Maven | 3.6+ | Dependency management |
| **Validation** | Hibernate Validator | 8.0+ | Input validation |
| **Utilities** | Lombok | 1.18.30 | Boilerplate reduction |

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- ☕ **Java 17+** - [Download OpenJDK](https://adoptium.net/)
- 🔨 **Maven 3.6+** - [Download Maven](https://maven.apache.org/download.cgi)
- 🍃 **MongoDB 4.4+** - [Download MongoDB](https://www.mongodb.com/try/download/community)

### System Requirements
- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: At least 2GB free space
- **OS**: Windows, macOS, or Linux

## 🚀 Quick Start

Follow these steps to get the application running in minutes:

### 1. 📥 Clone and Setup

```bash
# Navigate to project directory
cd thymeleaf-mongo-flyway-app

# Install dependencies
mvn clean install
```

### 2. 🗄️ Database Setup

```bash
# Start MongoDB (if not running)
mongod

# Create database (optional - will be created automatically)
mongo
> use thymeleafdb
```

### 3. ⚙️ Configuration

Update `src/main/resources/application.yml` if needed:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/thymeleafdb

mongock:
  change-logs-scan-package: com.example.thymeleafmongoflywayapp.migration
```

### 4. 🏃‍♂️ Run Application

```bash
# Option 1: Run with Maven
mvn spring-boot:run

# Option 2: Build and run JAR
mvn clean package
java -jar target/thymeleaf-mongo-flyway-app-0.0.1-SNAPSHOT.jar
```

### 5. 🌐 Access Application

Once running, access the application at:

| Page | URL | Description |
|------|-----|-------------|
| 🏠 **Home** | http://localhost:8080 | Main dashboard |
| 📊 **Dashboard** | http://localhost:8080/dashboard | Statistics and overview |
| 👥 **Users** | http://localhost:8080/users | User management |
| ➕ **Add User** | http://localhost:8080/users/new | Create new user |

> 💡 **Tip**: The application will automatically create 10 sample users on first startup!

## 📁 Project Structure

```
thymeleaf-mongo-flyway-app/
├── 📁 src/main/java/com/example/thymeleafmongoflywayapp/
│   ├── 📁 config/
│   │   └── MongockConfig.java              # Mongock configuration
│   ├── 📁 controller/
│   │   ├── HomeController.java             # Dashboard controller
│   │   └── UserController.java             # User CRUD controller
│   ├── 📁 entity/
│   │   └── User.java                       # User entity with MongoDB annotations
│   ├── 📁 migration/
│   │   └── CreateUsersCollectionMigration.java  # Database migration
│   ├── 📁 repository/
│   │   └── UserRepository.java             # MongoDB repository
│   ├── 📁 service/
│   │   └── UserService.java                # Business logic service
│   └── ThymeleafMongoFlywayAppApplication.java  # Main application class
├── 📁 src/main/resources/
│   ├── 📁 static/
│   │   ├── 📁 css/
│   │   │   └── style.css                   # Custom CSS styles
│   │   └── 📁 js/
│   │       └── app.js                      # Custom JavaScript
│   ├── 📁 templates/
│   │   ├── dashboard.html                  # Dashboard page
│   │   ├── layout.html                     # Base layout template
│   │   └── 📁 users/
│   │       ├── form.html                   # Add/Edit user form
│   │       ├── list.html                   # Users list page
│   │       └── view.html                   # User details page
│   └── application.yml                     # Application configuration
└── pom.xml                                 # Maven dependencies
```

### 📋 Key Components

| Component | Purpose | Key Features |
|-----------|---------|--------------|
| **🏠 Controllers** | Handle HTTP requests | RESTful endpoints, form handling |
| **📊 Services** | Business logic | CRUD operations, validation |
| **🗄️ Repositories** | Data access | MongoDB queries, custom methods |
| **📄 Entities** | Data models | MongoDB annotations, validation |
| **🎨 Templates** | UI rendering | Thymeleaf, responsive design |
| **⚙️ Config** | Application setup | Mongock, MongoDB configuration |

## 🎯 API Endpoints

| Method | Endpoint | Description | Features |
|--------|----------|-------------|----------|
| `GET` | `/` | Dashboard home | Statistics, quick actions |
| `GET` | `/dashboard` | Dashboard with stats | User count, system status |
| `GET` | `/users` | List users | Pagination, search, sorting |
| `GET` | `/users/new` | Show add user form | Form validation |
| `POST` | `/users/save` | Save new user | Input validation, error handling |
| `GET` | `/users/edit/{id}` | Show edit user form | Pre-populated form |
| `POST` | `/users/update/{id}` | Update existing user | Validation, conflict checking |
| `GET` | `/users/delete/{id}` | Delete user | Confirmation dialog |
| `GET` | `/users/view/{id}` | View user details | Read-only user info |

### 🔍 Query Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `page` | int | Page number (0-based) | `?page=0` |
| `size` | int | Items per page | `?size=10` |
| `sortBy` | string | Sort field | `?sortBy=name` |
| `sortDir` | string | Sort direction | `?sortDir=asc` |
| `search` | string | Search term | `?search=john` |

## 🎨 UI Features

### 📊 Dashboard
- **📈 Stats Cards**: Total users, active users, system status
- **⚡ Quick Actions**: Navigate to users, add new user
- **💻 System Info**: Database connection status
- **📱 Responsive Design**: Mobile-friendly layout

### 👥 Users Management
- **🔍 Search & Filter**: Search by name or email
- **📊 Sorting**: Sort by name, email, age, or creation date
- **📄 Pagination**: Navigate through large datasets
- **✏️ CRUD Operations**: Create, read, update, delete users
- **✅ Form Validation**: Client and server-side validation
- **📱 Responsive Table**: Mobile-optimized user list

### 🎨 Modern UI Elements
- **🎯 Bootstrap 5**: Latest Bootstrap framework
- **💎 Tailwind CSS**: Utility-first CSS framework
- **🎭 Bootstrap Icons**: Comprehensive icon set
- **✨ Custom Animations**: Smooth transitions and effects
- **🌙 Professional Theme**: Clean, modern design
- **📱 Mobile Responsive**: Works on all devices

## 🗄️ Database Schema

### 📄 Users Collection
```javascript
{
  "_id": ObjectId,                    // MongoDB unique identifier
  "name": String,                     // User's full name (required)
  "email": String,                    // Email address (required, unique)
  "age": Number,                      // User's age (required, min: 1)
  "created_at": Date,                 // Account creation timestamp
  "updated_at": Date                  // Last update timestamp
}
```

### 🔍 Database Indexes
| Index | Type | Purpose |
|-------|------|---------|
| `email` | Unique | Prevent duplicate emails |
| `name` | Regular | Optimize name searches |
| `age` | Regular | Enable age filtering |
| `created_at` | Regular | Sort by creation date |

### 📊 Sample Data
The application automatically creates 10 sample users on startup:
- John Doe (john.doe@example.com, 28)
- Jane Smith (jane.smith@example.com, 32)
- Mike Johnson (mike.johnson@example.com, 25)
- And 7 more sample users...

## 🔧 Configuration Options

### ⚙️ Application Properties
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
    cache: false                    # Disable cache for development
    prefix: classpath:/templates/   # Template location
    suffix: .html                   # Template extension
    encoding: UTF-8                 # Character encoding
    mode: HTML                      # Template mode

# Logging Configuration
logging:
  level:
    com.example.thymeleafmongoflywayapp: DEBUG
    org.springframework.data.mongodb.core.MongoTemplate: DEBUG
    io.mongock: INFO
```

### 🌍 Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/thymeleafdb` |
| `SERVER_PORT` | Application port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` |

## 🧪 Testing

### 🖱️ Manual Testing Checklist
1. **✅ Create User**: Navigate to `/users/new` and fill the form
2. **🔍 Search Users**: Use the search box to find users
3. **✏️ Edit User**: Click edit button and modify user details
4. **🗑️ Delete User**: Click delete button and confirm
5. **📄 Pagination**: Navigate through multiple pages
6. **📊 Sorting**: Click column headers to sort data
7. **📱 Responsive**: Test on mobile devices
8. **✅ Validation**: Test form validation with invalid data

### 🤖 Automated Testing
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run integration tests
mvn test -Dtest=*IntegrationTest
```

### 🧪 Test Coverage
- **Unit Tests**: Service layer, repository methods
- **Integration Tests**: Controller endpoints, database operations
- **UI Tests**: Thymeleaf template rendering
- **Validation Tests**: Form validation, error handling

## 🚀 Deployment

### 🐳 Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/thymeleaf-mongo-flyway-app-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 🏗️ Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - MONGODB_URI=mongodb://mongo:27017/thymeleafdb
    depends_on:
      - mongo
  
  mongo:
    image: mongo:4.4
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

volumes:
  mongo_data:
```

### ☁️ Production Configuration
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
    io.mongock: WARN
```

### 🌐 Cloud Deployment Options
- **AWS**: Elastic Beanstalk, ECS, or EC2
- **Google Cloud**: App Engine, Cloud Run, or GKE
- **Azure**: App Service or Container Instances
- **Heroku**: Platform as a Service
- **DigitalOcean**: Droplets or App Platform

## 📊 Performance Features

- **🗄️ Database Indexing**: Optimized queries with proper indexes
- **📄 Pagination**: Efficient handling of large datasets
- **💾 Caching**: Thymeleaf template caching
- **⚡ Lazy Loading**: Optimized data loading
- **🔗 Connection Pooling**: MongoDB connection optimization
- **📱 Responsive Design**: Mobile-first approach
- **🎯 Code Splitting**: Modular architecture

## 🔒 Security Features

- **✅ Input Validation**: Server-side validation with Hibernate Validator
- **🛡️ XSS Protection**: Thymeleaf automatic XSS protection
- **🔐 CSRF Protection**: Spring Security CSRF tokens
- **🚫 SQL Injection Prevention**: MongoDB parameterized queries
- **📝 Error Handling**: Secure error messages without sensitive data
- **🔍 Input Sanitization**: Clean user input processing

## 🎯 Best Practices Demonstrated

1. **🏗️ Clean Architecture**: Separation of concerns with layered architecture
2. **🌐 RESTful Design**: Proper HTTP methods and status codes
3. **⚠️ Error Handling**: Comprehensive error handling and user feedback
4. **📱 Responsive Design**: Mobile-first responsive design
5. **⚡ Performance Optimization**: Database indexing and query optimization
6. **🧹 Code Quality**: Lombok for boilerplate reduction
7. **📚 Documentation**: Comprehensive code documentation
8. **🧪 Testing**: Unit and integration testing strategies
9. **🔧 Configuration**: Environment-based configuration management
10. **📦 Modular Design**: Reusable components and services

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **🍴 Fork** the repository
2. **🌿 Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **💾 Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **📤 Push** to the branch (`git push origin feature/amazing-feature`)
5. **🔄 Open** a Pull Request

### 📋 Contribution Guidelines
- Follow the existing code style
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass
- Use meaningful commit messages

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 🆘 Support & Help

### 🐛 Bug Reports
- Create an issue with detailed steps to reproduce
- Include error messages and logs
- Specify your environment (OS, Java version, etc.)

### 💡 Feature Requests
- Describe the feature you'd like to see
- Explain why it would be useful
- Consider contributing the implementation

### 📚 Documentation
- Check this README for common questions
- Review the code comments for implementation details
- Look at the example usage in the templates

### 🔗 Useful Links
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Bootstrap Documentation](https://getbootstrap.com/docs/)

## 🔄 Version History

| Version | Date | Changes |
|---------|------|---------|
| **v1.0.0** | 2024-01-01 | Initial release with basic CRUD operations |
| **v1.1.0** | 2024-01-15 | Added search and pagination functionality |
| **v1.2.0** | 2024-01-30 | Enhanced UI with Bootstrap 5 and Tailwind CSS |
| **v1.3.0** | 2024-02-15 | Added Mongock migrations and comprehensive seed data |

---

## 🌟 Acknowledgments

- **Spring Boot Team** for the amazing framework
- **MongoDB** for the powerful NoSQL database
- **Bootstrap Team** for the responsive CSS framework
- **Thymeleaf Team** for the server-side templating engine
- **Open Source Community** for inspiration and support

---

<div align="center">

**🚀 Built with ❤️ using Spring Boot, MongoDB, and Thymeleaf**

[![Star](https://img.shields.io/github/stars/username/thymeleaf-mongo-flyway-app?style=social)](https://github.com/username/thymeleaf-mongo-flyway-app)
[![Fork](https://img.shields.io/github/forks/username/thymeleaf-mongo-flyway-app?style=social)](https://github.com/username/thymeleaf-mongo-flyway-app/fork)
[![Issues](https://img.shields.io/github/issues/username/thymeleaf-mongo-flyway-app)](https://github.com/username/thymeleaf-mongo-flyway-app/issues)

</div>
