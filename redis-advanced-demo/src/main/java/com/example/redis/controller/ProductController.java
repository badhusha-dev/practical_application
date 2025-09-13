package com.example.redis.controller;

import com.example.redis.entity.Product;
import com.example.redis.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String getAllProducts(Model model) {
        logger.info("GET /products - Fetching all products");
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("cacheInfo", "Products cached in Redis");
        return "products";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("GET /products/{} - Fetching product by id", id);
        Optional<Product> product = productService.getProductById(id);
        
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        logger.info("POST /products - Creating new product: {}", product.getName());
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        logger.info("PUT /products/{} - Updating product", id);
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("DELETE /products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        logger.info("GET /products/search?name={} - Searching products", name);
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/price-range")
    @ResponseBody
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        logger.info("GET /products/price-range?minPrice={}&maxPrice={} - Fetching products by price range", 
                   minPrice, maxPrice);
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
}
