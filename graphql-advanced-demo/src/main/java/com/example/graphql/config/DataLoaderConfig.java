package com.example.graphql.config;

import com.example.graphql.dto.CategoryDTO;
import com.example.graphql.dto.ProductDTO;
import com.example.graphql.entity.Category;
import com.example.graphql.entity.Product;
import com.example.graphql.repository.CategoryRepository;
import com.example.graphql.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataLoaderRegistrar;
import graphql.GraphQLContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoaderConfig implements DataLoaderRegistrar {
    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    
    @Override
    public void registerDataLoaders(DataLoaderRegistry registry, GraphQLContext context) {
        registry.register("categoryLoader", createCategoryDataLoader());
        registry.register("productLoader", createProductDataLoader());
    }
    
    private DataLoader<Long, CategoryDTO> createCategoryDataLoader() {
        BatchLoader<Long, CategoryDTO> categoryBatchLoader = categoryIds -> {
            log.debug("DataLoader: Loading categories for IDs: {}", categoryIds);
            
            return CompletableFuture.supplyAsync(() -> {
                List<Category> categories = categoryRepository.findAllById(categoryIds);
                return categoryIds.stream()
                        .map(id -> categories.stream()
                                .filter(category -> category.getId().equals(id))
                                .findFirst()
                                .map(this::toCategoryDTO)
                                .orElse(null))
                        .collect(Collectors.toList());
            });
        };
        
        return DataLoader.newDataLoader(categoryBatchLoader);
    }
    
    private DataLoader<Long, List<ProductDTO>> createProductDataLoader() {
        BatchLoader<Long, List<ProductDTO>> productBatchLoader = categoryIds -> {
            log.debug("DataLoader: Loading products for category IDs: {}", categoryIds);
            
            return CompletableFuture.supplyAsync(() -> {
                return categoryIds.stream()
                        .map(categoryId -> {
                            List<Product> products = productRepository.findByCategoryId(categoryId);
                            return products.stream()
                                    .map(this::toProductDTO)
                                    .collect(Collectors.toList());
                        })
                        .collect(Collectors.toList());
            });
        };
        
        return DataLoader.newDataLoader(productBatchLoader);
    }
    
    private CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                null, // Products will be loaded separately
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
    
    private ProductDTO toProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategoryId(),
                null, // Category will be loaded separately
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

}
