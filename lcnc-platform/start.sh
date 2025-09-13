#!/bin/bash

echo "Starting LCNC Platform..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

# Check if PostgreSQL is running
if ! pg_isready -h localhost -p 5432 &> /dev/null; then
    echo "PostgreSQL is not running. Please start PostgreSQL server."
    exit 1
fi

# Check if Redis is running
if ! redis-cli ping &> /dev/null; then
    echo "Redis is not running. Please start Redis server."
    exit 1
fi

echo "All prerequisites are met. Starting the application..."

# Build and run the application
mvn clean spring-boot:run
