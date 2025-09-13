# Swagger/OpenAPI Integration

This JWT Authentication project includes comprehensive Swagger/OpenAPI documentation integration.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Features

### API Documentation
- Complete API documentation for all authentication endpoints
- Interactive API testing interface
- Request/response examples
- Schema definitions for all DTOs

### Security Integration
- JWT Bearer token authentication support
- Secure endpoint testing with token authorization
- Token-based API access

### Endpoints Documented

#### Authentication Endpoints (`/auth`)
- `POST /auth/register` - Register a new user
- `POST /auth/login` - Authenticate user and get JWT tokens
- `POST /auth/refresh` - Refresh access token using refresh token
- `POST /auth/logout` - Logout user (requires authentication)
- `GET /auth/me` - Get current user information (requires authentication)

#### Health Check Endpoints
- `GET /health` - Application health status

## Usage

1. Start the application
2. Navigate to http://localhost:8080/swagger-ui.html
3. Use the "Authorize" button to add your JWT token for testing protected endpoints
4. Test API endpoints directly from the Swagger UI

## Configuration

The Swagger configuration is defined in `SwaggerConfig.java` and includes:
- API metadata and contact information
- JWT Bearer token security scheme
- Comprehensive endpoint documentation
- Request/response schema definitions
