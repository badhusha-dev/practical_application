package com.example.apibestpractices.dto;

import com.example.apibestpractices.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);
    
    // User mappings
    UserDto toUserDto(User user);
    List<UserDto> toUserDtoList(List<User> users);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User toUser(CreateUserRequest request);
    
    // Order mappings
    OrderDto toOrderDto(Order order);
    List<OrderDto> toOrderDtoList(List<Order> orders);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Order toOrder(CreateOrderRequest request);
    
    // OrderItem mappings
    OrderItemDto toOrderItemDto(OrderItem orderItem);
    List<OrderItemDto> toOrderItemDtoList(List<OrderItem> orderItems);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "totalPrice", expression = "java(request.getUnitPrice().multiply(java.math.BigDecimal.valueOf(request.getQuantity())))")
    OrderItem toOrderItem(CreateOrderRequest.CreateOrderItemRequest request);
    
    List<OrderItem> toOrderItemList(List<CreateOrderRequest.CreateOrderItemRequest> requests);
}
