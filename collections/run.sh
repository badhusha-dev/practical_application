#!/bin/bash

echo "========================================"
echo "Business Collections Demo"
echo "========================================"
echo ""
echo "This script will help you run the Spring Boot application."
echo ""
echo "Prerequisites:"
echo "1. Java 17 or higher installed"
echo "2. PostgreSQL database running"
echo "3. Database 'collections_demo' created"
echo ""
echo "========================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

echo "Java version:"
java -version
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi

echo "Maven version:"
mvn -version
echo ""

echo "========================================"
echo "Building the project..."
echo "========================================"
mvn clean install

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi

echo ""
echo "========================================"
echo "Starting the application..."
echo "========================================"
echo ""
echo "The application will be available at: http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

mvn spring-boot:run
