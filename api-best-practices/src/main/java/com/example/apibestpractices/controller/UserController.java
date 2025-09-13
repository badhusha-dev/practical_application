package com.example.apibestpractices.controller;

import com.example.apibestpractices.dto.CreateUserRequest;
import com.example.apibestpractices.dto.UserDto;
import com.example.apibestpractices.model.User;
import com.example.apibestpractices.service.UserService;
import com.example.apibestpractices.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
            summary = "Get all users",
            description = "Retrieve a paginated list of users with optional filtering and sorting",
            tags = {"User Management"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved users",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "message": "Users retrieved successfully",
                                        "data": {
                                            "content": [
                                                {
                                                    "id": 1,
                                                    "username": "john_doe",
                                                    "email": "john@example.com",
                                                    "role": "USER",
                                                    "isActive": true,
                                                    "createdAt": "2024-01-01T00:00:00Z",
                                                    "updatedAt": "2024-01-01T00:00:00Z",
                                                    "_links": {
                                                        "self": {"href": "/users/1"},
                                                        "orders": {"href": "/orders?userId=1&page=0&size=20&sort=id,desc"}
                                                    }
                                                }
                                            ],
                                            "page": {
                                                "size": 20,
                                                "number": 0,
                                                "totalElements": 100,
                                                "totalPages": 5
                                            },
                                            "_links": {
                                                "next": {"href": "/users?page=1&size=20&sort=id,asc"},
                                                "self": {"href": "/users"}
                                            }
                                        }
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too Many Requests - Rate limit exceeded")
    })
    public ResponseEntity<ApiResponse<PagedModel<EntityModel<UserDto>>>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field and direction (e.g., 'id,asc', 'username,desc')", example = "id,asc")
            @RequestParam(defaultValue = "id,asc") String sort,
            
            @Parameter(description = "Filter by user role", example = "USER")
            @RequestParam(required = false) String role,
            
            @Parameter(description = "Filter by active status", example = "true")
            @RequestParam(required = false) Boolean isActive,
            
            @Parameter(description = "Search term for username or email", example = "john")
            @RequestParam(required = false) String search) {
        
        log.info("GET /users - page: {}, size: {}, sort: {}, role: {}, isActive: {}, search: {}", 
                page, size, sort, role, isActive, search);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        
        // Parse role parameter
        User.Role roleEnum = null;
        if (role != null && !role.isEmpty()) {
            try {
                roleEnum = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid role: " + role));
            }
        }
        
        Page<UserDto> usersPage = userService.getUsersWithFilters(roleEnum, isActive, search, pageable);
        
        // Convert to HATEOAS PagedModel
        List<EntityModel<UserDto>> userModels = usersPage.getContent().stream()
                .map(userDto -> {
                    EntityModel<UserDto> model = EntityModel.of(userDto);
                    model.add(linkTo(methodOn(UserController.class).getUserById(userDto.getId())).withSelfRel());
                    model.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(userDto.getId(), 0, 20, "id,desc")).withRel("orders"));
                    return model;
                })
                .toList();
        
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                usersPage.getSize(),
                usersPage.getNumber(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
        
        PagedModel<EntityModel<UserDto>> pagedModel = PagedModel.of(userModels, pageMetadata);
        
        // Add navigation links
        if (usersPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(UserController.class).getAllUsers(
                    page + 1, size, sort, role, isActive, search)).withRel("next"));
        }
        if (usersPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(UserController.class).getAllUsers(
                    page - 1, size, sort, role, isActive, search)).withRel("prev"));
        }
        
        pagedModel.add(linkTo(UserController.class).withRel("self"));
        
        return ResponseEntity.ok(ApiResponse.success(pagedModel));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or (hasRole('USER') and @userService.isOwner(#id, authentication.name))")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a specific user by their unique identifier",
            tags = {"User Management"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too Many Requests - Rate limit exceeded")
    })
    public ResponseEntity<ApiResponse<EntityModel<UserDto>>> getUserById(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id) {
        log.info("GET /users/{}", id);
        
        UserDto user = userService.getUserById(id);
        
        EntityModel<UserDto> model = EntityModel.of(user);
        model.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        model.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(id, 0, 20, "id,desc")).withRel("orders"));
        model.add(linkTo(UserController.class).withRel("users"));
        
        return ResponseEntity.ok(ApiResponse.success(model));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new user",
            description = "Create a new user with the provided information. Supports idempotency via Idempotency-Key header.",
            tags = {"User Management"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - User already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too Many Requests - Rate limit exceeded")
    })
    public ResponseEntity<ApiResponse<EntityModel<UserDto>>> createUser(
            @Parameter(description = "User creation request", required = true)
            @Valid @RequestBody CreateUserRequest request,
            
            @Parameter(description = "Idempotency key for duplicate request prevention", example = "user-create-123")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        
        log.info("POST /users - username: {}, idempotencyKey: {}", request.getUsername(), idempotencyKey);
        
        UserDto user = userService.createUser(request);
        
        EntityModel<UserDto> model = EntityModel.of(user);
        model.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        model.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(user.getId(), 0, 20, "id,desc")).withRel("orders"));
        model.add(linkTo(UserController.class).withRel("users"));
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(model, "User created successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userService.isOwner(#id, authentication.name))")
    public ResponseEntity<ApiResponse<EntityModel<UserDto>>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        
        log.info("PUT /users/{} - username: {}", id, request.getUsername());
        
        UserDto user = userService.updateUser(id, request);
        
        EntityModel<UserDto> model = EntityModel.of(user);
        model.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        model.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(id, 0, 20, "id,desc")).withRel("orders"));
        model.add(linkTo(UserController.class).withRel("users"));
        
        return ResponseEntity.ok(ApiResponse.success(model, "User updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{}", id);
        
        userService.deleteUser(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        log.info("PATCH /users/{}/deactivate", id);
        
        userService.deactivateUser(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<UserStats>> getUserStats() {
        log.info("GET /users/stats");
        
        UserStats stats = new UserStats(
                userService.getUserCountByRole(User.Role.USER),
                userService.getUserCountByRole(User.Role.ADMIN),
                userService.getUserCountByRole(User.Role.MANAGER)
        );
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class UserStats {
        private long totalUsers;
        private long totalAdmins;
        private long totalManagers;
    }
}
