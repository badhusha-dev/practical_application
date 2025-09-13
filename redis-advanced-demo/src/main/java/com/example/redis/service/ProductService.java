package com.example.redis.service;

import com.example.redis.entity.Product;
import com.example.redis.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        logger.info("Fetching all products from database");
        return productRepository.findAll();
    }
    
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        logger.info("Fetching product with id: {} from database", id);
        return productRepository.findById(id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        logger.info("Creating new product: {}", product.getName());
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, Product product) {
        logger.info("Updating product with id: {}", id);
        if (productRepository.existsById(id)) {
            product.setId(id);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found with id: " + id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }
    
    @Cacheable(value = "products", key = "'search_' + #name")
    public List<Product> searchProductsByName(String name) {
        logger.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Cacheable(value = "products", key = "'price_' + #minPrice + '_' + #maxPrice")
    public List<Product> getProductsByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
        logger.info("Fetching products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
}
