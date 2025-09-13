package com.example.apibestpractices.dto;

import com.example.apibestpractices.model.Order;
import com.example.apibestpractices.model.OrderItem;
import com.example.apibestpractices.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-08T19:10:08+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class EntityMapperImpl implements EntityMapper {

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setCreatedAt( user.getCreatedAt() );
        userDto.setEmail( user.getEmail() );
        userDto.setFirstName( user.getFirstName() );
        userDto.setFullName( user.getFullName() );
        userDto.setId( user.getId() );
        userDto.setIsActive( user.getIsActive() );
        userDto.setLastName( user.getLastName() );
        userDto.setRole( user.getRole() );
        userDto.setUpdatedAt( user.getUpdatedAt() );
        userDto.setUsername( user.getUsername() );

        return userDto;
    }

    @Override
    public List<UserDto> toUserDtoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( users.size() );
        for ( User user : users ) {
            list.add( toUserDto( user ) );
        }

        return list;
    }

    @Override
    public User toUser(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( request.getEmail() );
        user.setFirstName( request.getFirstName() );
        user.setLastName( request.getLastName() );
        user.setRole( request.getRole() );
        user.setUsername( request.getUsername() );

        user.setIsActive( true );

        return user;
    }

    @Override
    public OrderDto toOrderDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setBillingAddress( order.getBillingAddress() );
        orderDto.setCreatedAt( order.getCreatedAt() );
        orderDto.setCurrency( order.getCurrency() );
        orderDto.setId( order.getId() );
        orderDto.setNotes( order.getNotes() );
        orderDto.setOrderItems( toOrderItemDtoList( order.getOrderItems() ) );
        orderDto.setOrderNumber( order.getOrderNumber() );
        orderDto.setShippingAddress( order.getShippingAddress() );
        orderDto.setStatus( order.getStatus() );
        orderDto.setTotalAmount( order.getTotalAmount() );
        orderDto.setUpdatedAt( order.getUpdatedAt() );

        return orderDto;
    }

    @Override
    public List<OrderDto> toOrderDtoList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderDto> list = new ArrayList<OrderDto>( orders.size() );
        for ( Order order : orders ) {
            list.add( toOrderDto( order ) );
        }

        return list;
    }

    @Override
    public Order toOrder(CreateOrderRequest request) {
        if ( request == null ) {
            return null;
        }

        Order order = new Order();

        order.setBillingAddress( request.getBillingAddress() );
        order.setCurrency( request.getCurrency() );
        order.setNotes( request.getNotes() );
        order.setOrderNumber( request.getOrderNumber() );
        order.setShippingAddress( request.getShippingAddress() );
        order.setTotalAmount( request.getTotalAmount() );

        order.setStatus( Order.OrderStatus.PENDING );

        return order;
    }

    @Override
    public OrderItemDto toOrderItemDto(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setId( orderItem.getId() );
        orderItemDto.setProductName( orderItem.getProductName() );
        orderItemDto.setProductSku( orderItem.getProductSku() );
        orderItemDto.setQuantity( orderItem.getQuantity() );
        orderItemDto.setTotalPrice( orderItem.getTotalPrice() );
        orderItemDto.setUnitPrice( orderItem.getUnitPrice() );

        return orderItemDto;
    }

    @Override
    public List<OrderItemDto> toOrderItemDtoList(List<OrderItem> orderItems) {
        if ( orderItems == null ) {
            return null;
        }

        List<OrderItemDto> list = new ArrayList<OrderItemDto>( orderItems.size() );
        for ( OrderItem orderItem : orderItems ) {
            list.add( toOrderItemDto( orderItem ) );
        }

        return list;
    }

    @Override
    public OrderItem toOrderItem(CreateOrderRequest.CreateOrderItemRequest request) {
        if ( request == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setProductName( request.getProductName() );
        orderItem.setProductSku( request.getProductSku() );
        orderItem.setQuantity( request.getQuantity() );
        orderItem.setUnitPrice( request.getUnitPrice() );

        orderItem.setTotalPrice( request.getUnitPrice().multiply(java.math.BigDecimal.valueOf(request.getQuantity())) );

        return orderItem;
    }

    @Override
    public List<OrderItem> toOrderItemList(List<CreateOrderRequest.CreateOrderItemRequest> requests) {
        if ( requests == null ) {
            return null;
        }

        List<OrderItem> list = new ArrayList<OrderItem>( requests.size() );
        for ( CreateOrderRequest.CreateOrderItemRequest createOrderItemRequest : requests ) {
            list.add( toOrderItem( createOrderItemRequest ) );
        }

        return list;
    }
}
