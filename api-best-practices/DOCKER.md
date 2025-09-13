# Docker Setup for API Best Practices

This document provides comprehensive instructions for running the API Best Practices application using Docker.

## 🐳 Docker Architecture

The application uses a multi-service Docker setup with the following components:

- **API Application**: Spring Boot application with all best practices
- **PostgreSQL**: Database for persistent data storage
- **Redis**: Cache and rate limiting storage
- **Nginx**: Reverse proxy and load balancer (production)

## 📋 Prerequisites

- Docker Desktop installed and running
- Docker Compose v3.8+
- At least 4GB RAM available for Docker
- Ports 8080, 5432, 6379 available on your system

## 🚀 Quick Start

### Development Environment

1. **Clone and navigate to the project**:
   ```bash
   git clone <repository-url>
   cd api-best-practices
   ```

2. **Start all services**:
   ```bash
   # Using the management script (Linux/Mac)
   chmod +x docker-manage.sh
   ./docker-manage.sh start
   
   # Or using Docker Compose directly
   docker-compose up -d
   ```

3. **Access the application**:
   - **API**: http://localhost:8080/api/v1
   - **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
   - **Health Check**: http://localhost:8080/api/v1/health

### Production Environment

1. **Create environment file**:
   ```bash
   cp env.example .env
   # Edit .env with your production values
   ```

2. **Start production services**:
   ```bash
   ./docker-manage.sh start-prod
   ```

## 🛠️ Management Scripts

### Linux/Mac (docker-manage.sh)

```bash
# Make executable
chmod +x docker-manage.sh

# Available commands
./docker-manage.sh build       # Build application image
./docker-manage.sh start       # Start development environment
./docker-manage.sh start-prod  # Start production environment
./docker-manage.sh stop        # Stop all services
./docker-manage.sh restart     # Restart services
./docker-manage.sh logs        # View application logs
./docker-manage.sh logs-all    # View all service logs
./docker-manage.sh status      # Show service status
./docker-manage.sh cleanup     # Clean up Docker resources
./docker-manage.sh help        # Show help
```

### Windows (docker-manage.bat)

```cmd
# Available commands
docker-manage.bat build       # Build application image
docker-manage.bat start       # Start development environment
docker-manage.bat start-prod  # Start production environment
docker-manage.bat stop        # Stop all services
docker-manage.bat restart     # Restart services
docker-manage.bat logs        # View application logs
docker-manage.bat logs-all    # View all service logs
docker-manage.bat status      # Show service status
docker-manage.bat cleanup     # Clean up Docker resources
docker-manage.bat help        # Show help
```

## 📁 Docker Files Overview

### Core Files

- **`Dockerfile`**: Multi-stage build for the Spring Boot application
- **`docker-compose.yml`**: Development environment configuration
- **`docker-compose.prod.yml`**: Production environment configuration
- **`.dockerignore`**: Files to exclude from Docker build context

### Configuration Files

- **`nginx/nginx.conf`**: Development Nginx configuration
- **`nginx/nginx-prod.conf`**: Production Nginx configuration
- **`src/main/resources/application-docker.yml`**: Docker-specific application config
- **`env.example`**: Environment variables template

## 🔧 Configuration

### Environment Variables

Create a `.env` file based on `env.example`:

```bash
# Database Configuration
POSTGRES_DB=api_best_practices
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password_here

# Redis Configuration
REDIS_PASSWORD=your_redis_password_here

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here_must_be_at_least_256_bits

# Application Configuration
SPRING_PROFILES_ACTIVE=docker
```

### Service Configuration

#### PostgreSQL
- **Port**: 5432
- **Database**: api_best_practices
- **Health Check**: Built-in pg_isready
- **Data Persistence**: Docker volume `postgres_data`

#### Redis
- **Port**: 6379
- **Password**: Configurable via environment
- **Health Check**: Redis ping command
- **Data Persistence**: Docker volume `redis_data`

#### API Application
- **Port**: 8080
- **Health Check**: HTTP endpoint `/api/v1/health`
- **Memory Limit**: 1GB (development), 2GB (production)
- **CPU Limit**: 1 core (development), 2 cores (production)

## 🔍 Monitoring and Health Checks

### Health Check Endpoints

- **Application**: `GET /api/v1/health`
- **PostgreSQL**: Built-in `pg_isready`
- **Redis**: Built-in `redis-cli ping`

### Monitoring Commands

```bash
# Check service status
docker-compose ps

# View application logs
docker-compose logs -f api-app

# View all service logs
docker-compose logs -f

# Check resource usage
docker stats
```

## 🚨 Troubleshooting

### Common Issues

1. **Port conflicts**:
   ```bash
   # Check if ports are in use
   netstat -tulpn | grep :8080
   netstat -tulpn | grep :5432
   netstat -tulpn | grep :6379
   ```

2. **Docker not running**:
   ```bash
   # Start Docker Desktop
   # Or restart Docker service
   sudo systemctl restart docker
   ```

3. **Out of memory**:
   ```bash
   # Increase Docker memory limit in Docker Desktop settings
   # Or reduce application memory usage
   ```

4. **Database connection issues**:
   ```bash
   # Check PostgreSQL logs
   docker-compose logs postgres
   
   # Check if database is ready
   docker-compose exec postgres pg_isready -U postgres
   ```

### Log Locations

- **Application logs**: Available via `docker-compose logs api-app`
- **Database logs**: Available via `docker-compose logs postgres`
- **Redis logs**: Available via `docker-compose logs redis`
- **Nginx logs**: Available via `docker-compose logs nginx`

## 🔒 Security Considerations

### Production Deployment

1. **Change default passwords** in `.env` file
2. **Use strong JWT secrets** (at least 256 bits)
3. **Enable HTTPS** in Nginx configuration
4. **Use Docker secrets** for sensitive data
5. **Regular security updates** for base images

### Network Security

- Services communicate via internal Docker network
- Only necessary ports exposed to host
- Nginx provides additional security headers
- Rate limiting configured at Nginx level

## 📊 Performance Optimization

### Resource Limits

- **Development**: 1GB RAM, 1 CPU core
- **Production**: 2GB RAM, 2 CPU cores
- **Database**: 2GB RAM, 2 CPU cores
- **Redis**: 512MB RAM, 1 CPU core

### Caching Strategy

- **Application**: Redis-based caching
- **Nginx**: Static content caching
- **Database**: Connection pooling with HikariCP

## 🔄 Backup and Recovery

### Database Backup

```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres api_best_practices > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U postgres api_best_practices < backup.sql
```

### Volume Backup

```bash
# Backup volumes
docker run --rm -v api-best-practices_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .
docker run --rm -v api-best-practices_redis_data:/data -v $(pwd):/backup alpine tar czf /backup/redis_backup.tar.gz -C /data .
```

## 📈 Scaling

### Horizontal Scaling

```bash
# Scale application instances
docker-compose -f docker-compose.prod.yml up -d --scale api-app=3
```

### Load Balancing

Nginx automatically load balances between multiple application instances using the `least_conn` strategy.

## 🆘 Support

For issues related to Docker setup:

1. Check the logs: `./docker-manage.sh logs-all`
2. Verify service status: `./docker-manage.sh status`
3. Check Docker resources: `docker stats`
4. Review this documentation
5. Check Docker Desktop logs

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [Redis Docker Image](https://hub.docker.com/_/redis)
- [Nginx Docker Image](https://hub.docker.com/_/nginx)
