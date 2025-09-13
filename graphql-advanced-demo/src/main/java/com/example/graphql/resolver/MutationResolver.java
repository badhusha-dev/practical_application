package com.example.graphql.resolver;

import com.example.graphql.dto.CategoryDTO;
import com.example.graphql.dto.ProductDTO;
import com.example.graphql.dto.UserDTO;
import com.example.graphql.service.CategoryService;
import com.example.graphql.service.ProductService;
import com.example.graphql.service.UserService;
import com.example.graphql.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MutationResolver {
    
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final JwtService jwtService;
    
    @MutationMapping
    public UserDTO createUser(@Argument String username, 
                             @Argument String email, 
                             @Argument String password) {
        log.info("GraphQL Mutation: createUser - username: {}", username);
        return userService.createUser(username, email, password);
    }
    
    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public ProductDTO createProduct(@Argument String name, 
                                  @Argument BigDecimal price, 
                                  @Argument Long categoryId) {
        log.info("GraphQL Mutation: createProduct - name: {}, price: {}, categoryId: {}", 
                name, price, categoryId);
        return productService.createProduct(name, price, categoryId);
    }
    
    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public CategoryDTO createCategory(@Argument String name) {
        log.info("GraphQL Mutation: createCategory - name: {}", name);
        return categoryService.createCategory(name);
    }
    
    @MutationMapping
    public String login(@Argument String username, @Argument String password) {
        log.info("GraphQL Mutation: login - username: {}", username);
        
        if (userService.authenticateUser(username, password)) {
            return jwtService.generateToken(username);
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
