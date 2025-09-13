#!/bin/bash

# Docker management script for API Best Practices project

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to build the application
build_app() {
    print_status "Building the application..."
    docker-compose build --no-cache
    print_success "Application built successfully!"
}

# Function to start all services
start_all() {
    print_status "Starting all services..."
    docker-compose up -d
    print_success "All services started!"
    print_status "Application will be available at: http://localhost:8080/api/v1"
    print_status "Swagger UI will be available at: http://localhost:8080/api/v1/swagger-ui.html"
    print_status "Health check: http://localhost:8080/api/v1/health"
}

# Function to start in development mode
start_dev() {
    print_status "Starting in development mode..."
    docker-compose -f docker-compose.yml up -d
    print_success "Development environment started!"
}

# Function to start in production mode
start_prod() {
    print_status "Starting in production mode..."
    if [ ! -f .env ]; then
        print_warning ".env file not found. Creating from template..."
        cp env.example .env
        print_warning "Please update .env file with your production values!"
    fi
    docker-compose -f docker-compose.prod.yml up -d
    print_success "Production environment started!"
}

# Function to stop all services
stop_all() {
    print_status "Stopping all services..."
    docker-compose down
    print_success "All services stopped!"
}

# Function to view logs
view_logs() {
    print_status "Viewing application logs..."
    docker-compose logs -f api-app
}

# Function to view all logs
view_all_logs() {
    print_status "Viewing all service logs..."
    docker-compose logs -f
}

# Function to restart services
restart() {
    print_status "Restarting services..."
    docker-compose restart
    print_success "Services restarted!"
}

# Function to clean up
cleanup() {
    print_status "Cleaning up Docker resources..."
    docker-compose down -v --remove-orphans
    docker system prune -f
    print_success "Cleanup completed!"
}

# Function to show status
show_status() {
    print_status "Service status:"
    docker-compose ps
}

# Function to show help
show_help() {
    echo "Docker Management Script for API Best Practices"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build       Build the application image"
    echo "  start       Start all services (development)"
    echo "  start-dev   Start in development mode"
    echo "  start-prod  Start in production mode"
    echo "  stop        Stop all services"
    echo "  restart     Restart all services"
    echo "  logs        View application logs"
    echo "  logs-all    View all service logs"
    echo "  status      Show service status"
    echo "  cleanup     Clean up Docker resources"
    echo "  help        Show this help message"
    echo ""
}

# Main script logic
main() {
    check_docker
    
    case "${1:-help}" in
        build)
            build_app
            ;;
        start|start-dev)
            start_dev
            ;;
        start-prod)
            start_prod
            ;;
        stop)
            stop_all
            ;;
        restart)
            restart
            ;;
        logs)
            view_logs
            ;;
        logs-all)
            view_all_logs
            ;;
        status)
            show_status
            ;;
        cleanup)
            cleanup
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
