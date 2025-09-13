@echo off
REM Local development startup script for Windows
REM This script starts the application with local profile (no Redis required)

echo Starting API Best Practices Application in LOCAL mode...
echo This mode disables Redis and uses in-memory rate limiting
echo.

REM Set the active profile to local
set SPRING_PROFILES_ACTIVE=local

REM Start the application
mvn spring-boot:run

echo.
echo Application started!
echo API: http://localhost:8080/api/v1
echo Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
echo Health Check: http://localhost:8080/api/v1/health
