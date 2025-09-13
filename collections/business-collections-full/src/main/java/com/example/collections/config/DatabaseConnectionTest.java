package com.example.collections.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnectionTest {
    
    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    public void testConnection() {
        System.out.println("=== Testing PostgreSQL Connection ===");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Password: " + (password != null ? "***" : "null"));
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("✅ PostgreSQL connection successful!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Version: " + connection.getMetaData().getDriverVersion());
        } catch (SQLException e) {
            System.err.println("❌ PostgreSQL connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            // Provide troubleshooting suggestions
            System.err.println("\n=== Troubleshooting Suggestions ===");
            System.err.println("1. Check if PostgreSQL service is running");
            System.err.println("2. Verify username and password are correct");
            System.err.println("3. Ensure database 'business_collections' exists");
            System.err.println("4. Check PostgreSQL authentication settings in pg_hba.conf");
            System.err.println("5. Try connecting with pgAdmin first");
        }
    }
}
