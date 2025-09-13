# Multithreading Demo - Spring Boot

This Spring Boot project demonstrates different approaches to multithreading in Spring Boot applications.

## Project Structure

```
src/main/java/com/example/multithreading/
├── MultithreadingDemoApplication.java
├── config/
│   └── AsyncConfig.java
├── controller/
│   └── TaskController.java
└── service/
    └── TaskService.java
```

## Features

### 1. Async Tasks Endpoint (`GET /tasks/async`)
- Triggers three async tasks: email sending, report generation, and notification sending
- Returns immediately with "Tasks started!" message
- Tasks run in parallel using Spring's `@Async` annotation
- Console logs show tasks running on different threads

### 2. Executor Tasks Endpoint (`GET /tasks/executor`)
- Uses `ExecutorService` to run all three tasks in parallel
- Waits for all tasks to complete before returning response
- Returns combined results from all tasks
- Console logs show tasks executed by thread pool

## Task Details

- **Email Sending**: Simulated with 2-second delay
- **Report Generation**: Simulated with 3-second delay  
- **Notification Sending**: Simulated with 1-second delay

## Running the Application

1. **Build the project:**
   ```bash
   mvn clean compile
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Test the endpoints:**
   - Async tasks: `http://localhost:8080/tasks/async`
   - Executor tasks: `http://localhost:8080/tasks/executor`
   - **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## Expected Console Output

### For `/tasks/async`:
```
Main Thread: Starting async tasks...
Main Thread: All async tasks started, returning immediately!
AsyncTask-1: Sending email...
AsyncTask-2: Generating report...
AsyncTask-3: Sending notification...
AsyncTask-3: Notification sent successfully!
AsyncTask-1: Email sent successfully!
AsyncTask-2: Report generated successfully!
```

### For `/tasks/executor`:
```
Main Thread: Starting executor tasks...
pool-1-thread-1: Sending email...
pool-1-thread-2: Generating report...
pool-1-thread-3: Sending notification...
pool-1-thread-3: Notification sent successfully!
pool-1-thread-1: Email sent successfully!
pool-1-thread-2: Report generated successfully!
Main Thread: All executor tasks completed!
```

## Configuration

The application uses a custom `ThreadPoolTaskExecutor` configured in `AsyncConfig.java`:
- Core pool size: 5 threads
- Max pool size: 10 threads
- Queue capacity: 25 tasks
- Thread name prefix: "AsyncTask-"

## Dependencies

- Spring Boot 3.2.0
- Spring Web Starter
- SpringDoc OpenAPI (Swagger UI)
- Java 17

## Swagger UI

The application includes Swagger UI for easy API testing and documentation:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

You can use Swagger UI to:
- View all available endpoints
- Test endpoints directly from the browser
- See detailed API documentation
- Understand request/response formats
