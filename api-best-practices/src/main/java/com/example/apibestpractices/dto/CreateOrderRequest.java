package com.example.apibestpractices.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new order")
public class CreateOrderRequest {
    
    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user placing the order", example = "1", required = true)
    private Long userId;
    
    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    @Schema(description = "Unique order number", example = "ORD-2024-001", maxLength = 50)
    private String orderNumber;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @Schema(description = "Total order amount", example = "99.99", minimum = "0.01")
    private BigDecimal totalAmount;
    
    @Size(max = 3, message = "Currency must not exceed 3 characters")
    @Schema(description = "Currency code", example = "USD", maxLength = 3, defaultValue = "USD")
    private String currency = "USD";
    
    @Schema(description = "Shipping address", example = "123 Main St, City, State 12345")
    private String shippingAddress;
    
    @Schema(description = "Billing address", example = "123 Main St, City, State 12345")
    private String billingAddress;
    
    @Schema(description = "Additional notes for the order", example = "Please handle with care")
    private String notes;
    
    @Schema(description = "List of order items")
    private List<CreateOrderItemRequest> orderItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order item details")
    public static class CreateOrderItemRequest {
        
        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        @Schema(description = "Name of the product", example = "Laptop Computer", maxLength = 255)
        private String productName;
        
        @Size(max = 100, message = "Product SKU must not exceed 100 characters")
        @Schema(description = "Product SKU", example = "LAPTOP-001", maxLength = 100)
        private String productSku;
        
        @NotNull(message = "Quantity is required")
        @Schema(description = "Quantity of the product", example = "2", minimum = "1")
        private Integer quantity;
        
        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
        @Schema(description = "Unit price of the product", example = "49.99", minimum = "0.01")
        private BigDecimal unitPrice;
    }
}
