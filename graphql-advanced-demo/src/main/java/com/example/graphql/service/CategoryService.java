package com.example.graphql.service;

import com.example.graphql.dto.CategoryDTO;
import com.example.graphql.entity.Category;
import com.example.graphql.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Cacheable(value = "categories", key = "'all'")
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.info("Fetching all categories from database");
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::toCategoryDTO)
                .toList();
    }
    
    @Cacheable(value = "categories", key = "#id")
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryById(Long id) {
        log.info("Fetching category with id: {} from database", id);
        return categoryRepository.findById(id)
                .map(this::toCategoryDTO);
    }
    
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(String name) {
        log.info("Creating new category: {}", name);
        
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category already exists: " + name);
        }
        
        Category category = new Category(name);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Category created successfully with id: {}", savedCategory.getId());
        return toCategoryDTO(savedCategory);
    }
    
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO updateCategory(Long id, String name) {
        log.info("Updating category with id: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category already exists: " + name);
        }
        
        category.setName(name);
        Category savedCategory = categoryRepository.save(category);
        
        return toCategoryDTO(savedCategory);
    }
    
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    private CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                null, // Products will be loaded separately via DataLoader
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
