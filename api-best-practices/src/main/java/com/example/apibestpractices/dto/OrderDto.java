package com.example.apibestpractices.dto;

import com.example.apibestpractices.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private Order.OrderStatus status;
    private BigDecimal totalAmount;
    private String currency;
    private String shippingAddress;
    private String billingAddress;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<OrderItemDto> orderItems;
}
