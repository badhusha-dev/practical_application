@echo off
echo ========================================
echo Business Collections Demo
echo ========================================
echo.
echo This script will help you run the Spring Boot application.
echo.
echo Prerequisites:
echo 1. Java 17 or higher installed
echo 2. PostgreSQL database running
echo 3. Database 'collections_demo' created
echo.
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

echo Java version:
java -version
echo.

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher
    pause
    exit /b 1
)

echo Maven version:
mvn -version
echo.

echo ========================================
echo Building the project...
echo ========================================
mvn clean install

if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo Starting the application...
echo ========================================
echo.
echo The application will be available at: http://localhost:8080
echo.
echo Press Ctrl+C to stop the application
echo.

mvn spring-boot:run

pause
