package com.example.graphql.dto;

import com.example.graphql.entity.Category;
import com.example.graphql.entity.Product;
import com.example.graphql.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);
    
    UserDTO toUserDTO(User user);
    
    List<UserDTO> toUserDTOs(List<User> users);
    
    @Mapping(target = "products", ignore = true)
    CategoryDTO toCategoryDTO(Category category);
    
    List<CategoryDTO> toCategoryDTOs(List<Category> categories);
    
    @Mapping(target = "category", ignore = true)
    ProductDTO toProductDTO(Product product);
    
    List<ProductDTO> toProductDTOs(List<Product> products);
    
    // Reverse mappings for creating entities from DTOs
    User toUser(UserDTO userDTO);
    
    Category toCategory(CategoryDTO categoryDTO);
    
    Product toProduct(ProductDTO productDTO);
}
