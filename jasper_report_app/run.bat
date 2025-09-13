@echo off
echo Starting Jasper Reports Demo Application...
echo.
echo Make sure PostgreSQL is running and the database 'jasper_report_db' exists.
echo Update database credentials in src/main/resources/application.yml if needed.
echo.
echo Starting application on http://localhost:8080
echo.
echo Available endpoints:
echo   GET /reports/users?format=pdf
echo   GET /reports/users?format=xlsx
echo   GET /reports/orders?format=pdf
echo   GET /reports/orders?from=2024-01-01^&to=2024-01-31^&format=xlsx
echo.
mvn spring-boot:run

