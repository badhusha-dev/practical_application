# JWT Authentication with PostgreSQL

A Spring Boot application that implements JWT-based authentication with PostgreSQL database integration.

## Features

- JWT-based authentication with HS256 algorithm
- Access and Refresh token support
- PostgreSQL database with Flyway migrations
- BCrypt password encoding with strength validation
- Role-based authorization (ROLE_USER, ROLE_ADMIN)
- RESTful API endpoints with validation
- Global exception handling
- API documentation with Swagger/OpenAPI
- Health check endpoint
- Comprehensive error handling

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional, for PostgreSQL)

## Database Setup

### Option 1: Using Docker (Recommended)

```bash
# Start PostgreSQL container
docker run --name postgres-jwt -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=jwt_auth -p 5432:5432 -d postgres:15
```

### Option 2: Local PostgreSQL

1. Create a database named `jwt_auth`
2. Update `application.yml` with your database credentials if different

## Running the Application

1. **Clone and navigate to the project:**
   ```bash
   cd jwt-auth-db
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### 1. Health Check
```http
GET /health
```

**Response:**
```json
{
  "status": "UP",
  "message": "JWT Authentication Service is running",
  "timestamp": 1703123456789
}
```

### 2. Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "Secret123"
}
```

**Response:**
```json
{
  "message": "User registered successfully",
  "username": "john",
  "roles": ["ROLE_USER"]
}
```

### 3. Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "Secret123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john",
  "authorities": ["ROLE_USER"]
}
```

### 4. Refresh Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john"
}
```

### 5. Get Current User (Protected)
```http
GET /auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response:**
```json
{
  "id": 1,
  "username": "john",
  "roles": ["ROLE_USER"]
}
```

### 6. Logout
```http
POST /auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response:**
```json
{
  "error": "Logout successful"
}
```

## Testing with curl

### Health Check:
```bash
curl -X GET http://localhost:8080/health
```

### Register a user:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "password": "Secret123"}'
```

### Login:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "password": "Secret123"}'
```

### Refresh token (replace REFRESH_TOKEN with the actual refresh token):
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "REFRESH_TOKEN"}'
```

### Get user info (replace ACCESS_TOKEN with the actual access token):
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer ACCESS_TOKEN"
```

### Logout:
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer ACCESS_TOKEN"
```

## API Documentation

The application includes Swagger/OpenAPI documentation. After starting the application, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

This provides interactive documentation for all API endpoints with request/response examples.

## Project Structure

```
src/main/java/com/example/jwtauth/
├── controller/
│   └── AuthController.java          # REST endpoints
├── entity/
│   ├── User.java                    # User entity
│   └── Role.java                    # Role entity
├── repository/
│   ├── UserRepository.java          # User data access
│   └── RoleRepository.java          # Role data access
├── service/
│   └── UserService.java             # Business logic
├── security/
│   ├── JwtUtil.java                 # JWT utilities
│   ├── JwtFilter.java               # JWT filter
│   ├── JwtUserDetailsService.java   # User details service
│   └── SecurityConfig.java          # Security configuration
└── JwtAuthDbApplication.java        # Main application class

src/main/resources/
├── application.yml                   # Application configuration
└── db/migration/
    ├── V1__Create_user_and_role_tables.sql
    └── V2__Insert_default_roles.sql
```

## Configuration

Key configuration in `application.yml`:
- Database connection (PostgreSQL)
- JPA settings (validate schema, show SQL)
- Flyway migrations enabled
- Server port: 8080

## Security Features

- JWT tokens with 1-hour expiration
- BCrypt password hashing
- Stateless session management
- Role-based access control
- Protected endpoints (except /auth/register and /auth/login)

## Error Handling

- 400 Bad Request: Invalid registration/login data
- 401 Unauthorized: Invalid/missing JWT token
- 500 Internal Server Error: Server-side issues

## JWT Token Details

- Algorithm: HS256
- Secret: Configurable via environment variable `JWT_SECRET` (256-bit minimum)
- Access Token Expiration: 1 hour (configurable via `JWT_ACCESS_EXPIRATION`)
- Refresh Token Expiration: 24 hours (configurable via `JWT_REFRESH_EXPIRATION`)
- Claims: username and token type

## Environment Variables

You can configure the following environment variables:

- `JWT_SECRET`: JWT signing secret (minimum 32 characters for security)
- `JWT_ACCESS_EXPIRATION`: Access token expiration in milliseconds (default: 3600000 = 1 hour)
- `JWT_REFRESH_EXPIRATION`: Refresh token expiration in milliseconds (default: 86400000 = 24 hours)

Example:
```bash
export JWT_SECRET="your-very-secure-secret-key-here-with-32-characters-minimum"
export JWT_ACCESS_EXPIRATION=1800000  # 30 minutes
export JWT_REFRESH_EXPIRATION=604800000  # 7 days
```
