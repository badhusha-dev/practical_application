@echo off
REM Docker management script for API Best Practices project (Windows)

setlocal enabledelayedexpansion

REM Function to print colored output
:print_status
echo [INFO] %~1
goto :eof

:print_success
echo [SUCCESS] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof

REM Function to check if Docker is running
:check_docker
docker info >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker is not running. Please start Docker and try again."
    exit /b 1
)
goto :eof

REM Function to build the application
:build_app
call :print_status "Building the application..."
docker-compose build --no-cache
if errorlevel 1 (
    call :print_error "Failed to build application"
    exit /b 1
)
call :print_success "Application built successfully!"
goto :eof

REM Function to start all services
:start_all
call :print_status "Starting all services..."
docker-compose up -d
if errorlevel 1 (
    call :print_error "Failed to start services"
    exit /b 1
)
call :print_success "All services started!"
call :print_status "Application will be available at: http://localhost:8080/api/v1"
call :print_status "Swagger UI will be available at: http://localhost:8080/api/v1/swagger-ui.html"
call :print_status "Health check: http://localhost:8080/api/v1/health"
goto :eof

REM Function to start in development mode
:start_dev
call :print_status "Starting in development mode..."
docker-compose -f docker-compose.yml up -d
if errorlevel 1 (
    call :print_error "Failed to start development environment"
    exit /b 1
)
call :print_success "Development environment started!"
goto :eof

REM Function to start in production mode
:start_prod
call :print_status "Starting in production mode..."
if not exist .env (
    call :print_warning ".env file not found. Creating from template..."
    copy env.example .env
    call :print_warning "Please update .env file with your production values!"
)
docker-compose -f docker-compose.prod.yml up -d
if errorlevel 1 (
    call :print_error "Failed to start production environment"
    exit /b 1
)
call :print_success "Production environment started!"
goto :eof

REM Function to stop all services
:stop_all
call :print_status "Stopping all services..."
docker-compose down
call :print_success "All services stopped!"
goto :eof

REM Function to view logs
:view_logs
call :print_status "Viewing application logs..."
docker-compose logs -f api-app
goto :eof

REM Function to view all logs
:view_all_logs
call :print_status "Viewing all service logs..."
docker-compose logs -f
goto :eof

REM Function to restart services
:restart
call :print_status "Restarting services..."
docker-compose restart
call :print_success "Services restarted!"
goto :eof

REM Function to clean up
:cleanup
call :print_status "Cleaning up Docker resources..."
docker-compose down -v --remove-orphans
docker system prune -f
call :print_success "Cleanup completed!"
goto :eof

REM Function to show status
:show_status
call :print_status "Service status:"
docker-compose ps
goto :eof

REM Function to show help
:show_help
echo Docker Management Script for API Best Practices
echo.
echo Usage: %~nx0 [COMMAND]
echo.
echo Commands:
echo   build       Build the application image
echo   start       Start all services (development)
echo   start-dev   Start in development mode
echo   start-prod  Start in production mode
echo   stop        Stop all services
echo   restart     Restart all services
echo   logs        View application logs
echo   logs-all    View all service logs
echo   status      Show service status
echo   cleanup     Clean up Docker resources
echo   help        Show this help message
echo.
goto :eof

REM Main script logic
:main
call :check_docker
if errorlevel 1 exit /b 1

if "%1"=="" goto :help
if "%1"=="help" goto :help
if "%1"=="--help" goto :help
if "%1"=="-h" goto :help
if "%1"=="build" goto :build_app
if "%1"=="start" goto :start_dev
if "%1"=="start-dev" goto :start_dev
if "%1"=="start-prod" goto :start_prod
if "%1"=="stop" goto :stop_all
if "%1"=="restart" goto :restart
if "%1"=="logs" goto :view_logs
if "%1"=="logs-all" goto :view_all_logs
if "%1"=="status" goto :show_status
if "%1"=="cleanup" goto :cleanup

call :print_error "Unknown command: %1"
call :show_help
exit /b 1

:help
call :show_help
exit /b 0

REM Run main function
call :main %*
