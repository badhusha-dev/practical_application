# OAuth Demo - Spring Boot with Google Authentication

This is a Spring Boot application demonstrating OAuth2 authentication with Google as the identity provider.

## Features

- 🔐 OAuth2 authentication with Google
- 🎨 Beautiful responsive UI with Bootstrap
- 📱 Mobile-friendly design
- 🔒 Secure session management
- 📊 User information API endpoint
- 🚀 Spring Boot 3.2.0 with Java 17

## Project Structure

```
oauth-demo/
├── src/main/java/com/example/oauth/
│   ├── OauthDemoApplication.java
│   ├── controller/
│   │   └── HomeController.java
│   └── security/
│       └── SecurityConfig.java
├── src/main/resources/
│   ├── application.yml
│   └── templates/
│       ├── index.html
│       └── secured.html
└── pom.xml
```

## Setup Instructions

### 1. Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
5. Set application type to "Web application"
6. Add authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google`
7. Copy the Client ID and Client Secret

### 2. Configure Application

Update `src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_ACTUAL_GOOGLE_CLIENT_ID
            client-secret: YOUR_ACTUAL_GOOGLE_CLIENT_SECRET
```

### 3. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/oauth-demo-0.0.1-SNAPSHOT.jar
```

## Usage

1. **Public Page**: Visit `http://localhost:8080/`
   - Shows welcome page with "Login with Google" button
   - No authentication required

2. **Login Flow**: Click "Login with Google"
   - Redirects to Google OAuth2 consent screen
   - After consent, redirects to `/secured`

3. **Secured Page**: Visit `http://localhost:8080/secured`
   - Shows personalized welcome message
   - Only accessible after authentication
   - Displays user information

4. **User API**: Visit `http://localhost:8080/user`
   - Returns JSON with authenticated user details
   - Includes name, email, ID, and profile picture

## Endpoints

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| GET | `/` | Public welcome page | None |
| GET | `/secured` | Protected page with user info | Required |
| GET | `/user` | User details API (JSON) | Required |
| POST | `/logout` | Logout and redirect to home | Required |

## Security Features

- OAuth2 authentication with Google
- CSRF protection enabled
- Secure session management
- Automatic logout on session expiry
- Protected routes configuration

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Spring Security** - Security framework
- **Spring OAuth2 Client** - OAuth2 integration
- **Thymeleaf** - Template engine
- **Bootstrap 5** - UI framework
- **Maven** - Build tool
- **Java 17** - Programming language

## Troubleshooting

### Common Issues

1. **"Invalid client" error**
   - Verify Client ID and Client Secret in `application.yml`
   - Ensure redirect URI matches exactly: `http://localhost:8080/login/oauth2/code/google`

2. **"Access blocked" error**
   - Check if Google+ API is enabled in Google Cloud Console
   - Verify OAuth consent screen is configured

3. **Application won't start**
   - Ensure Java 17 is installed
   - Check if port 8080 is available

### Debug Mode

To enable debug logging, add to `application.yml`:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

## License

This project is for educational purposes and demonstrates OAuth2 integration with Spring Boot.
