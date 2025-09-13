package com.example.graphql.resolver;

import com.example.graphql.dto.CategoryDTO;
import com.example.graphql.dto.ProductDTO;
import com.example.graphql.service.CategoryService;
import com.example.graphql.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DataLoaderResolver {
    
    private final CategoryService categoryService;
    private final ProductService productService;
    
    @SchemaMapping(typeName = "Product", field = "category")
    public CompletableFuture<CategoryDTO> category(ProductDTO product, DataLoader<Long, CategoryDTO> categoryLoader) {
        log.debug("DataLoader: Loading category for product: {}", product.getName());
        return categoryLoader.load(product.getCategoryId());
    }
    
    @SchemaMapping(typeName = "Category", field = "products")
    public CompletableFuture<List<ProductDTO>> products(CategoryDTO category, DataLoader<Long, List<ProductDTO>> productLoader) {
        log.debug("DataLoader: Loading products for category: {}", category.getName());
        return productLoader.load(category.getId());
    }
}
