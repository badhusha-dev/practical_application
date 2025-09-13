package com.example.graphql.service;

import com.example.graphql.dto.ProductDTO;
import com.example.graphql.entity.Product;
import com.example.graphql.event.ProductCreatedEvent;
import com.example.graphql.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Cacheable(value = "products", key = "'all'")
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.info("Fetching all products from database");
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::toProductDTO)
                .toList();
    }
    
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ProductDTO> getProductById(Long id) {
        log.info("Fetching product with id: {} from database", id);
        return productRepository.findById(id)
                .map(this::toProductDTO);
    }
    
    @Cacheable(value = "products", key = "'category_' + #categoryId")
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        log.info("Fetching products by category id: {} from database", categoryId);
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(this::toProductDTO)
                .toList();
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(String name, BigDecimal price, Long categoryId) {
        log.info("Creating new product: {} with category id: {}", name, categoryId);
        
        Product product = new Product(name, price, categoryId);
        Product savedProduct = productRepository.save(product);
        
        ProductDTO productDTO = toProductDTO(savedProduct);
        
        // Publish event for subscription
        eventPublisher.publishEvent(new ProductCreatedEvent(productDTO));
        
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return productDTO;
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO updateProduct(Long id, String name, BigDecimal price, Long categoryId) {
        log.info("Updating product with id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setName(name);
        product.setPrice(price);
        product.setCategoryId(categoryId);
        
        Product savedProduct = productRepository.save(product);
        return toProductDTO(savedProduct);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    @Cacheable(value = "products", key = "'search_' + #name")
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
                .map(this::toProductDTO)
                .toList();
    }
    
    @Cacheable(value = "products", key = "'price_' + #minPrice + '_' + #maxPrice")
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching products by price range: {} - {}", minPrice, maxPrice);
        List<Product> products = productRepository.findByPriceRange(minPrice, maxPrice);
        return products.stream()
                .map(this::toProductDTO)
                .toList();
    }
    
    private ProductDTO toProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategoryId(),
                null, // Category will be loaded separately via DataLoader
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
