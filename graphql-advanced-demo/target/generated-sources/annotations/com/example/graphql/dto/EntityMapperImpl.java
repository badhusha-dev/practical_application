package com.example.graphql.dto;

import com.example.graphql.entity.Category;
import com.example.graphql.entity.Product;
import com.example.graphql.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-15T21:57:19+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class EntityMapperImpl implements EntityMapper {

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setCreatedAt( user.getCreatedAt() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setId( user.getId() );
        userDTO.setUpdatedAt( user.getUpdatedAt() );
        userDTO.setUsername( user.getUsername() );

        return userDTO;
    }

    @Override
    public List<UserDTO> toUserDTOs(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( users.size() );
        for ( User user : users ) {
            list.add( toUserDTO( user ) );
        }

        return list;
    }

    @Override
    public CategoryDTO toCategoryDTO(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setCreatedAt( category.getCreatedAt() );
        categoryDTO.setId( category.getId() );
        categoryDTO.setName( category.getName() );
        categoryDTO.setUpdatedAt( category.getUpdatedAt() );

        return categoryDTO;
    }

    @Override
    public List<CategoryDTO> toCategoryDTOs(List<Category> categories) {
        if ( categories == null ) {
            return null;
        }

        List<CategoryDTO> list = new ArrayList<CategoryDTO>( categories.size() );
        for ( Category category : categories ) {
            list.add( toCategoryDTO( category ) );
        }

        return list;
    }

    @Override
    public ProductDTO toProductDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setCategoryId( product.getCategoryId() );
        productDTO.setCreatedAt( product.getCreatedAt() );
        productDTO.setId( product.getId() );
        productDTO.setName( product.getName() );
        productDTO.setPrice( product.getPrice() );
        productDTO.setUpdatedAt( product.getUpdatedAt() );

        return productDTO;
    }

    @Override
    public List<ProductDTO> toProductDTOs(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductDTO> list = new ArrayList<ProductDTO>( products.size() );
        for ( Product product : products ) {
            list.add( toProductDTO( product ) );
        }

        return list;
    }

    @Override
    public User toUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setCreatedAt( userDTO.getCreatedAt() );
        user.setEmail( userDTO.getEmail() );
        user.setId( userDTO.getId() );
        user.setUpdatedAt( userDTO.getUpdatedAt() );
        user.setUsername( userDTO.getUsername() );

        return user;
    }

    @Override
    public Category toCategory(CategoryDTO categoryDTO) {
        if ( categoryDTO == null ) {
            return null;
        }

        Category category = new Category();

        category.setCreatedAt( categoryDTO.getCreatedAt() );
        category.setId( categoryDTO.getId() );
        category.setName( categoryDTO.getName() );
        category.setUpdatedAt( categoryDTO.getUpdatedAt() );

        return category;
    }

    @Override
    public Product toProduct(ProductDTO productDTO) {
        if ( productDTO == null ) {
            return null;
        }

        Product product = new Product();

        product.setCategory( toCategory( productDTO.getCategory() ) );
        product.setCategoryId( productDTO.getCategoryId() );
        product.setCreatedAt( productDTO.getCreatedAt() );
        product.setId( productDTO.getId() );
        product.setName( productDTO.getName() );
        product.setPrice( productDTO.getPrice() );
        product.setUpdatedAt( productDTO.getUpdatedAt() );

        return product;
    }
}
