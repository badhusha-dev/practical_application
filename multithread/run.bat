@echo off
echo Multithreading Demo - Spring Boot
echo ================================
echo.
echo This script helps you run the Spring Boot application.
echo.
echo Prerequisites:
echo - Java 17 or higher must be installed
echo - Maven must be installed and in PATH
echo.
echo If you don't have Maven installed, you can:
echo 1. Download Maven from https://maven.apache.org/download.cgi
echo 2. Add Maven to your PATH environment variable
echo 3. Or use an IDE like IntelliJ IDEA or Eclipse
echo.
echo Building the project...
mvn clean compile
if %errorlevel% neq 0 (
    echo.
    echo Error: Maven is not installed or not in PATH.
    echo Please install Maven and try again.
    pause
    exit /b 1
)
echo.
echo Build successful!
echo.
echo Running the application...
echo The application will start on http://localhost:8080
echo.
echo Available endpoints:
echo - GET http://localhost:8080/tasks/async
echo - GET http://localhost:8080/tasks/executor
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo.
echo Press Ctrl+C to stop the application
echo.
mvn spring-boot:run
