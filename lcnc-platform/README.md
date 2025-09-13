# LCNC Platform

A comprehensive Low-Code No-Code platform built with Spring Boot, featuring workflow management, REST/GraphQL APIs, JWT security, and Camunda BPM integration.

## Features

### Core Functionality
- **Workflow Management**: Upload, deploy, and manage BPMN workflows
- **REST APIs**: Complete CRUD operations for apps and workflows
- **GraphQL Support**: Flexible query interface for data access
- **JWT Security**: Role-based authentication and authorization
- **Redis Caching**: High-performance workflow state management
- **Database Integration**: PostgreSQL with Flyway migrations
- **Camunda Integration**: Professional workflow execution engine
- **Thymeleaf UI**: User-friendly web interface

### Entities
- **AppDefinition**: Store application configurations as JSON
- **WorkflowDefinition**: Manage BPMN workflow definitions
- **Workflow Instances**: Track running workflow executions

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: PostgreSQL with Flyway migrations
- **Cache**: Redis for workflow state management
- **Security**: Spring Security with JWT tokens
- **Workflow Engine**: Camunda BPM 7.20.0
- **APIs**: REST + GraphQL
- **Frontend**: Thymeleaf with Bootstrap 5
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

## Installation & Setup

### 1. Database Setup
```sql
-- Create database and user
CREATE DATABASE lcnc_platform;
CREATE USER lcnc_user WITH PASSWORD 'lcnc_password';
GRANT ALL PRIVILEGES ON DATABASE lcnc_platform TO lcnc_user;
```

### 2. Redis Setup
```bash
# Start Redis server
redis-server
```

### 3. Application Configuration
Update `src/main/resources/application.yml` with your database and Redis connection details:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lcnc_platform
    username: lcnc_user
    password: lcnc_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. Build and Run
```bash
# Clone the repository
git clone <repository-url>
cd lcnc-platform

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login with username/password
- `POST /api/auth/register` - Register new user

### App Definitions
- `GET /api/apps` - Get all apps
- `GET /api/apps/{id}` - Get app by ID
- `POST /api/apps` - Create new app
- `PUT /api/apps/{id}` - Update app
- `DELETE /api/apps/{id}` - Delete app

### Workflow Definitions
- `GET /api/workflows` - Get all workflows
- `GET /api/workflows/{id}` - Get workflow by ID
- `POST /api/workflows` - Create new workflow
- `POST /api/workflows/upload` - Upload BPMN file
- `PUT /api/workflows/{id}` - Update workflow
- `DELETE /api/workflows/{id}` - Delete workflow
- `POST /api/workflows/{id}/deploy` - Deploy workflow
- `POST /api/workflows/{id}/undeploy` - Undeploy workflow

### Workflow Execution
- `POST /api/workflow-execution/start/{processDefinitionKey}` - Start workflow instance
- `POST /api/workflow-execution/complete-task/{taskId}` - Complete task
- `GET /api/workflow-execution/tasks/{processInstanceId}` - Get active tasks
- `GET /api/workflow-execution/state/{processInstanceId}` - Get workflow state

### GraphQL
- `POST /graphql` - GraphQL endpoint
- `GET /graphiql` - GraphQL playground

## Web Interface

### Main Pages
- `/` - Home page with platform overview
- `/workflow` - Workflow dashboard
- `/workflow/upload` - Upload BPMN workflows
- `/workflow/{id}/start` - Start workflow instance

### Admin Interfaces
- `/camunda` - Camunda Admin interface
- `/camunda/app` - Camunda Web App

## Default Users

The application comes with pre-configured users:

- **Admin**: username `admin`, password `admin`
- **User**: username `user`, password `user`

## Database Schema

### App Definitions Table
```sql
CREATE TABLE app_definitions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    config_json TEXT NOT NULL,
    description TEXT,
    version VARCHAR(50) DEFAULT '1.0.0',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Workflow Definitions Table
```sql
CREATE TABLE workflow_definitions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    bpmn_xml TEXT NOT NULL,
    description TEXT,
    version VARCHAR(50) DEFAULT '1.0.0',
    deployed BOOLEAN DEFAULT FALSE,
    camunda_deployment_id VARCHAR(255),
    process_definition_key VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Usage Examples

### 1. Upload a Workflow
```bash
curl -X POST http://localhost:8080/api/workflows/upload \
  -H "Authorization: Bearer <jwt-token>" \
  -F "file=@workflow.bpmn" \
  -F "name=My Workflow" \
  -F "description=Sample workflow"
```

### 2. Deploy a Workflow
```bash
curl -X POST http://localhost:8080/api/workflows/1/deploy \
  -H "Authorization: Bearer <jwt-token>"
```

### 3. Start Workflow Instance
```bash
curl -X POST http://localhost:8080/api/workflow-execution/start/myProcess \
  -H "Authorization: Bearer <jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"variable1": "value1", "variable2": "value2"}'
```

### 4. GraphQL Query
```graphql
query {
  workflows {
    id
    name
    version
    deployed
  }
}
```

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/lcnc/platform/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST and Web controllers
│   │   ├── entity/          # JPA entities
│   │   ├── graphql/         # GraphQL resolvers
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # Security configuration
│   │   ├── service/         # Business logic services
│   │   └── LcncPlatformApplication.java
│   └── resources/
│       ├── db/migration/    # Flyway migration scripts
│       ├── graphql/          # GraphQL schema
│       ├── templates/        # Thymeleaf templates
│       └── application.yml   # Application configuration
```

### Adding New Features
1. Create entity classes in `entity/` package
2. Add repository interfaces in `repository/` package
3. Implement service classes in `service/` package
4. Create REST controllers in `controller/` package
5. Add GraphQL resolvers if needed
6. Create database migrations in `db/migration/`
7. Add Thymeleaf templates for UI

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please open an issue in the repository or contact the development team.
