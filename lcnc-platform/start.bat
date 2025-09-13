@echo off
echo Starting LCNC Platform...

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java is not installed. Please install Java 17 or higher.
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven is not installed. Please install Maven 3.6 or higher.
    pause
    exit /b 1
)

echo All prerequisites are met. Starting the application...

REM Build and run the application
mvn clean spring-boot:run

pause
