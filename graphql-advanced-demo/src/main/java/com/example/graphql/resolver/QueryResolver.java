package com.example.graphql.resolver;

import com.example.graphql.dto.CategoryDTO;
import com.example.graphql.dto.ProductDTO;
import com.example.graphql.dto.UserDTO;
import com.example.graphql.service.CategoryService;
import com.example.graphql.service.ProductService;
import com.example.graphql.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryResolver {
    
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    
    @QueryMapping
    public List<UserDTO> users() {
        log.info("GraphQL Query: users");
        return userService.getAllUsers();
    }
    
    @QueryMapping
    public List<ProductDTO> products() {
        log.info("GraphQL Query: products");
        return productService.getAllProducts();
    }
    
    @QueryMapping
    public List<CategoryDTO> categories() {
        log.info("GraphQL Query: categories");
        return categoryService.getAllCategories();
    }
}
