# API Best Practices Demo

A comprehensive Spring Boot application demonstrating advanced REST API design patterns and best practices.

## Features

### 🚀 **REST API Best Practices**
- **Clear Naming**: RESTful endpoints following `/api/v1/users` and `/api/v1/orders` conventions
- **Versioning**: API versioning with `/api/v1/` prefix, ready for future `/api/v2/` changes
- **HATEOAS**: Cross-resource linking with Spring HATEOAS for discoverable APIs
- **Pagination**: Offset-based pagination with metadata (page, size, total, navigation links)
- **Sorting & Filtering**: Query parameters for `sort=name,asc&filter=role:admin`
- **Standardized Responses**: Consistent JSON response format with `ApiResponse<T>` wrapper

### 🔒 **Security & Authentication**
- **JWT Authentication**: Token-based authentication with configurable expiration
- **OAuth2 Support**: Resource server configuration for third-party integration
- **Role-Based Access Control**: `@PreAuthorize` annotations with role and ownership checks
- **CORS Configuration**: Configurable cross-origin resource sharing

### ⚡ **Advanced Features**
- **Idempotency**: `Idempotency-Key` header support for safe request retries
- **Rate Limiting**: Bucket4j-based rate limiting (100 requests/minute per user)
- **Request Validation**: Hibernate Validator with custom error messages
- **Global Exception Handling**: Centralized error handling with standardized error responses
- **Database Migrations**: Flyway for version-controlled database schema changes

### 📊 **Data Management**
- **JPA Entities**: Proper entity relationships with `User` → `Order` → `OrderItem`
- **MapStruct DTOs**: Automatic mapping between entities and DTOs
- **Repository Pattern**: Custom queries with filtering and pagination support
- **Transaction Management**: Proper `@Transactional` annotations

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

## Setup Instructions

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE api_best_practices;

-- Create user (optional)
CREATE USER api_user WITH PASSWORD 'api_password';
GRANT ALL PRIVILEGES ON DATABASE api_best_practices TO api_user;
```

### 2. Redis Setup
```bash
# Start Redis server
redis-server

# Verify Redis is running
redis-cli ping
```

### 3. Application Configuration
Update `src/main/resources/application.yml` if needed:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/api_best_practices
    username: postgres
    password: postgres
  redis:
    host: localhost
    port: 6379

app:
  security:
    jwt:
      secret: your-secret-key-here
      expiration: 86400000 # 24 hours
  rate-limit:
    requests-per-minute: 100
  idempotency:
    enabled: true
    ttl-hours: 24
```

### 4. Run Application
```bash
# Navigate to project directory
cd api-best-practices

# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/api-best-practices-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Authentication
The application uses JWT tokens. Include the token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

### Users API

#### Get All Users (with pagination, sorting, filtering)
```http
GET /api/v1/users?page=0&size=20&sort=name,asc&role=USER&isActive=true&search=john
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "john.doe",
        "email": "john.doe@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "role": "USER",
        "isActive": true,
        "createdAt": "2024-01-15T10:30:00Z",
        "_links": {
          "self": {"href": "/api/v1/users/1"},
          "orders": {"href": "/api/v1/orders/user/1?page=0&size=20&sort=id,desc"}
        }
      }
    ],
    "page": {
      "size": 20,
      "number": 0,
      "totalElements": 1,
      "totalPages": 1
    },
    "_links": {
      "self": {"href": "/api/v1/users?page=0&size=20&sort=name,asc"},
      "next": {"href": "/api/v1/users?page=1&size=20&sort=name,asc"}
    }
  }
}
```

#### Create User (with idempotency)
```http
POST /api/v1/users
Content-Type: application/json
Idempotency-Key: unique-key-123

{
  "username": "newuser",
  "email": "newuser@example.com",
  "firstName": "New",
  "lastName": "User",
  "role": "USER"
}
```

#### Update User
```http
PUT /api/v1/users/1
Content-Type: application/json

{
  "username": "updateduser",
  "email": "updated@example.com",
  "firstName": "Updated",
  "lastName": "User",
  "role": "USER"
}
```

#### Delete User
```http
DELETE /api/v1/users/1
```

### Orders API

#### Get All Orders (with filtering)
```http
GET /api/v1/orders?page=0&size=20&sort=createdAt,desc&userId=1&status=CONFIRMED&minAmount=50&maxAmount=200
```

#### Create Order (with idempotency)
```http
POST /api/v1/orders
Content-Type: application/json
Idempotency-Key: order-unique-key-456

{
  "userId": 1,
  "orderNumber": "ORD-001",
  "totalAmount": 99.99,
  "currency": "USD",
  "shippingAddress": "123 Main St, Anytown, USA",
  "orderItems": [
    {
      "productName": "Wireless Headphones",
      "productSku": "WH-001",
      "quantity": 1,
      "unitPrice": 99.99
    }
  ]
}
```

#### Update Order Status
```http
PATCH /api/v1/orders/1/status?status=SHIPPED
```

#### Get Orders by User
```http
GET /api/v1/orders/user/1?page=0&size=20&sort=id,desc
```

## Key Features Demonstrated

### 1. **Idempotency**
- Use `Idempotency-Key` header for POST/PUT requests
- Prevents duplicate operations on retries
- Stores response for 24 hours (configurable)

```bash
# First request
curl -X POST /api/v1/users \
  -H "Idempotency-Key: user-123" \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","firstName":"Test","lastName":"User"}'

# Retry with same key - returns cached response
curl -X POST /api/v1/users \
  -H "Idempotency-Key: user-123" \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","firstName":"Test","lastName":"User"}'
```

### 2. **Rate Limiting**
- 100 requests per minute per user/IP
- Returns `429 Too Many Requests` when exceeded
- Uses Bucket4j with Redis backend

### 3. **Pagination & Filtering**
```bash
# Pagination
GET /api/v1/users?page=0&size=10

# Sorting
GET /api/v1/users?sort=createdAt,desc

# Filtering
GET /api/v1/users?role=ADMIN&isActive=true

# Search
GET /api/v1/users?search=john
```

### 4. **HATEOAS Links**
Every response includes relevant links:
```json
{
  "_links": {
    "self": {"href": "/api/v1/users/1"},
    "orders": {"href": "/api/v1/orders/user/1"},
    "users": {"href": "/api/v1/users"}
  }
}
```

### 5. **Error Handling**
Standardized error responses:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/users",
  "traceId": "abc123-def456",
  "validationErrors": {
    "email": "Email must be valid",
    "username": "Username is required"
  }
}
```

## Security

### JWT Token Structure
```json
{
  "sub": "username",
  "roles": ["ROLE_USER"],
  "iat": 1642248000,
  "exp": 1642334400
}
```

### Role-Based Access
- **ADMIN**: Full access to all endpoints
- **MANAGER**: Read access to stats, limited write access
- **USER**: Access only to own resources

### Method Security Examples
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@PreAuthorize("hasRole('USER') and @userService.isOwner(#id, authentication.name)")
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

### Orders Table
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    shipping_address TEXT,
    billing_address TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

### Idempotency Keys Table
```sql
CREATE TABLE idempotency_keys (
    key_value VARCHAR(255) PRIMARY KEY,
    request_hash VARCHAR(64) NOT NULL,
    response_body TEXT,
    http_status INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at TIMESTAMPTZ NOT NULL
);
```

## Configuration Options

### Application Properties
```yaml
app:
  security:
    jwt:
      secret: ${JWT_SECRET:default-secret}
      expiration: 86400000
      refresh-expiration: 604800000
  
  rate-limit:
    enabled: true
    requests-per-minute: 100
    burst-capacity: 200
    refill-tokens: 100
    refill-period: 60
  
  idempotency:
    enabled: true
    key-header: Idempotency-Key
    ttl-hours: 24
  
  pagination:
    default-page-size: 20
    max-page-size: 100
  
  api:
    version: v1
    base-path: /api/v1
```

## Monitoring & Health Checks

### Actuator Endpoints
- **Health**: http://localhost:8080/api/v1/actuator/health
- **Info**: http://localhost:8080/api/v1/actuator/info
- **Metrics**: http://localhost:8080/api/v1/actuator/metrics
- **Prometheus**: http://localhost:8080/api/v1/actuator/prometheus

### Key Metrics
- HTTP request count and duration
- Database connection pool metrics
- Rate limiting violations
- Idempotency key usage

## Testing

### Sample Test Requests

#### 1. Create User with Idempotency
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-user-123" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### 2. Get Users with Pagination
```bash
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=5&sort=createdAt,desc" \
  -H "Authorization: Bearer <your-jwt-token>"
```

#### 3. Create Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Idempotency-Key: order-123" \
  -d '{
    "userId": 1,
    "orderNumber": "ORD-001",
    "totalAmount": 99.99,
    "orderItems": [
      {
        "productName": "Test Product",
        "quantity": 1,
        "unitPrice": 99.99
      }
    ]
  }'
```

#### 4. Test Rate Limiting
```bash
# Make multiple requests quickly to trigger rate limit
for i in {1..110}; do
  curl -X GET http://localhost:8080/api/v1/users
done
```

## Best Practices Demonstrated

1. **RESTful Design**: Proper HTTP methods, status codes, and resource naming
2. **API Versioning**: Clear versioning strategy with `/api/v1/` prefix
3. **Error Handling**: Consistent error responses with proper HTTP status codes
4. **Security**: JWT authentication, role-based access control, CORS configuration
5. **Performance**: Pagination, caching, rate limiting, database optimization
6. **Reliability**: Idempotency, transaction management, validation
7. **Maintainability**: Clean architecture, separation of concerns, comprehensive logging
8. **Documentation**: Clear API documentation, code comments, README

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
