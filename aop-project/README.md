# AOP Demo Project

This is a Spring Boot project demonstrating Aspect-Oriented Programming (AOP) functionality.

## Project Structure

```
com.example.aopdemo/
├── controller/
│   └── UserController.java
├── service/
│   └── UserService.java
├── aspect/
│   └── LoggingAspect.java
├── config/
│   └── OpenApiConfig.java
└── AopDemoApplication.java
```

## Dependencies

- Spring Boot 3.2.0
- Spring Web
- Spring AOP
- SpringDoc OpenAPI (Swagger UI)

## How to Run

1. Make sure you have Java 17+ installed
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. The application will start on `http://localhost:8080`

## API Documentation

The application includes Swagger UI for interactive API documentation:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

You can explore and test all API endpoints directly from the Swagger UI interface.

## API Endpoints

### GET /users/{id}
Returns user information by ID.

**Example:**
```bash
curl http://localhost:8080/users/1
```

**Expected Response:**
```
User with ID: 1
```

**Console Output:**
```
Before advice: Executing method: UserService.getUserById
Around advice: Starting execution of UserService.getUserById
AfterReturning advice: Method UserService.getUserById returned: User with ID: 1
After advice: Method completed: UserService.getUserById
Around advice: Completed UserService.getUserById in Xms
```

### POST /users?name={name}
Creates a new user with the specified name.

**Example:**
```bash
curl -X POST "http://localhost:8080/users?name=Shahul"
```

**Expected Response:**
```
Created user: Shahul
```

**Console Output:**
```
Before advice: Executing method: UserService.createUser
Around advice: Starting execution of UserService.createUser
AfterReturning advice: Method UserService.createUser returned: Created user: Shahul
After advice: Method completed: UserService.createUser
Around advice: Completed UserService.createUser in Xms
```

### GET /users/error
Intentionally generates a RuntimeException to demonstrate AOP error handling.

**Example:**
```bash
curl http://localhost:8080/users/error
```

**Expected Response:**
```
HTTP 500 Internal Server Error
```

**Console Output:**
```
Before advice: Executing method: UserService.generateError
Around advice: Starting execution of UserService.generateError
AfterThrowing advice: Method UserService.generateError threw exception: Simulated error
After advice: Method completed: UserService.generateError
Around advice: Exception in UserService.generateError after Xms
```

## AOP Implementation

The `LoggingAspect` class demonstrates all five types of Spring AOP advice:

### Advice Types:

1. **@Before** - Executes before method execution
   - Logs method name before execution
   - Pointcut: `execution(* com.example.aopdemo.service.*.*(..))`

2. **@After** - Executes after method execution (success or failure)
   - Logs method completion
   - Always executes regardless of outcome

3. **@AfterReturning** - Executes after successful method completion
   - Logs method name and return value
   - Only executes when method returns normally

4. **@AfterThrowing** - Executes when method throws an exception
   - Logs method name and exception details
   - Only executes when method throws an exception

5. **@Around** - Wraps the entire method execution
   - Logs execution start and end
   - Measures and logs execution time
   - Can control method execution flow

## Swagger Integration

The project includes Swagger UI for API documentation:

- **Configuration:** `OpenApiConfig.java` - Customizes API documentation
- **Annotations:** Controller methods are annotated with OpenAPI annotations
- **UI:** Interactive documentation available at `/swagger-ui.html`
- **JSON:** OpenAPI specification available at `/api-docs`

## Testing

You can test the endpoints using curl, Postman, or any HTTP client. The aspect will automatically log method executions to the console whenever service methods are called.
