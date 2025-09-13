# Redis Advanced Demo

A comprehensive Spring Boot application demonstrating advanced Redis features including caching, session management, rate limiting, and pub/sub messaging.

## Features

### 🚀 Redis Integration
- **Caching Layer**: Product queries cached in Redis for improved performance
- **Session Store**: Distributed login sessions stored in Redis
- **Rate Limiting**: Redis-based atomic counters with TTL for API protection
- **Pub/Sub Messaging**: Real-time notifications using Redis channels

### 🗄️ Database
- **PostgreSQL**: Persistent data storage for users and products
- **Flyway**: Database migration management
- **JPA/Hibernate**: ORM for data access

### 🎨 Frontend
- **Thymeleaf**: Server-side templating
- **Bootstrap**: Modern, responsive UI
- **Interactive Demos**: Test all Redis features through web interface

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

## Setup Instructions

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE redis_demo;

-- Create user (optional)
CREATE USER redis_user WITH PASSWORD 'redis_pass';
GRANT ALL PRIVILEGES ON DATABASE redis_demo TO redis_user;
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
    url: jdbc:postgresql://localhost:5432/redis_demo
    username: postgres
    password: postgres
  redis:
    host: localhost
    port: 6379
```

### 4. Run Application
```bash
# Navigate to project directory
cd redis-advanced-demo

# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/redis-advanced-demo-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Products (with Redis Caching)
- `GET /products` - Get all products (cached)
- `GET /products/{id}` - Get product by ID (cached)
- `POST /products` - Create product (invalidates cache)
- `PUT /products/{id}` - Update product (invalidates cache)
- `DELETE /products/{id}` - Delete product (invalidates cache)
- `GET /products/search?name={name}` - Search products by name (cached)
- `GET /products/price-range?minPrice={min}&maxPrice={max}` - Get products by price range (cached)

### Authentication (Redis Sessions)
- `GET /auth/login` - Login page
- `POST /auth/login` - Authenticate user (stores session in Redis)
- `GET /auth/me` - Get current user from Redis session
- `POST /auth/logout` - Logout user (removes session from Redis)
- `POST /auth/register` - Register new user

### Rate Limiting (Redis Counters)
- `GET /api/data` - Protected endpoint (max 5 requests/minute)
- `GET /api/rate-limit-status` - Check rate limit status
- `POST /api/reset-rate-limit` - Reset rate limit for user

### Notifications (Redis Pub/Sub)
- `GET /notifications` - Notifications page
- `POST /notifications/publish` - Publish message to Redis channel
- `GET /notifications/messages` - Get all received messages
- `POST /notifications/clear` - Clear all messages
- `GET /notifications/subscribe` - Subscribe to Redis channel

## Demo Credentials

Use these credentials to test the application:

| Username | Password |
|----------|----------|
| admin    | admin123 |
| user1    | password123 |
| user2    | password456 |
| testuser | testpass |

## Testing Redis Features

### 1. Caching Demo
1. Visit `/products` - First load fetches from database
2. Refresh page - Subsequent loads served from Redis cache
3. Create/update/delete products - Cache automatically invalidated

### 2. Session Management Demo
1. Visit `/auth/login` and login with demo credentials
2. Session stored in Redis (check with `redis-cli KEYS "*session*"`)
3. Visit `/auth/me` to verify session retrieval
4. Logout removes session from Redis

### 3. Rate Limiting Demo
1. Login to get a user session
2. Visit `/api/data` multiple times
3. After 5 requests, you'll get rate limit exceeded error
4. Wait 1 minute or use `/api/reset-rate-limit` to reset

### 4. Pub/Sub Demo
1. Visit `/notifications` page
2. Open multiple browser tabs/windows
3. Send a message from one tab
4. Message appears in all tabs in real-time

## Redis Keys Structure

The application uses the following Redis key patterns:

```
# Caching
products:all                    # All products cache
products:{id}                   # Individual product cache
products:search_{name}          # Search results cache
products:price_{min}_{max}      # Price range cache

# Sessions
spring:session:sessions:{sessionId}  # User sessions

# Rate Limiting
rate_limit:{userId}             # User rate limit counters

# Pub/Sub
notifications                   # Channel for real-time messages
```

## Monitoring Redis

### Check Redis Data
```bash
# Connect to Redis CLI
redis-cli

# List all keys
KEYS *

# Check specific cache
GET products:all

# Monitor Redis commands
MONITOR

# Check Redis info
INFO memory
INFO stats
```

### Check Session Data
```bash
# List session keys
KEYS spring:session:*

# Get session details
HGETALL spring:session:sessions:{sessionId}
```

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Browser   │    │  Spring Boot    │    │   PostgreSQL    │
│                 │◄──►│   Application   │◄──►│   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │      Redis      │
                       │                 │
                       │ • Cache Layer   │
                       │ • Session Store │
                       │ • Rate Limiting │
                       │ • Pub/Sub      │
                       └─────────────────┘
```

## Performance Benefits

- **Caching**: Reduces database load by 80-90% for read-heavy operations
- **Sessions**: Enables horizontal scaling with stateless application servers
- **Rate Limiting**: Protects APIs from abuse with minimal overhead
- **Pub/Sub**: Real-time features without polling or WebSocket complexity

## Troubleshooting

### Common Issues

1. **Redis Connection Failed**
   - Ensure Redis server is running: `redis-cli ping`
   - Check Redis host/port in application.yml

2. **Database Connection Failed**
   - Verify PostgreSQL is running
   - Check database credentials in application.yml
   - Ensure database exists

3. **Cache Not Working**
   - Check Redis connection
   - Verify @EnableCaching annotation
   - Check cache configuration

4. **Sessions Not Persisting**
   - Verify Redis connection
   - Check @EnableRedisHttpSession annotation
   - Ensure session timeout configuration

### Logs
Application logs are configured to show Redis operations:
```yaml
logging:
  level:
    com.example.redis: DEBUG
    org.springframework.cache: DEBUG
    org.springframework.session: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
