package com.example.graphql.event;

import com.example.graphql.dto.ProductDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProductCreatedEvent extends ApplicationEvent {
    
    private final ProductDTO product;
    
    public ProductCreatedEvent(ProductDTO product) {
        super(product);
        this.product = product;
    }
}
