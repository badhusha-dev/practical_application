package com.example.aopdemo.controller;

import com.example.aopdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves user information by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public String getUserById(
        @Parameter(description = "User ID", example = "1")
        @PathVariable int id
    ) {
        return userService.getUserById(id);
    }

    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the specified name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public String createUser(
        @Parameter(description = "User name", example = "Shahul")
        @RequestParam String name
    ) {
        return userService.createUser(name);
    }

    @GetMapping("/error")
    @Operation(
        summary = "Generate error",
        description = "Intentionally generates a RuntimeException to demonstrate AOP error handling"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Simulated error generated")
    })
    public String generateError() {
        return userService.generateError();
    }
}
