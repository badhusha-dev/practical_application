package com.example.apibestpractices.service;

import com.example.apibestpractices.dto.CreateOrderRequest;
import com.example.apibestpractices.dto.EntityMapper;
import com.example.apibestpractices.dto.OrderDto;
import com.example.apibestpractices.model.Order;
import com.example.apibestpractices.model.OrderItem;
import com.example.apibestpractices.model.User;
import com.example.apibestpractices.repository.OrderRepository;
import com.example.apibestpractices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        log.debug("Fetching all orders with pagination: {}", pageable);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::mapOrderToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersWithFilters(Long userId, Order.OrderStatus status, 
                                             OffsetDateTime startDate, OffsetDateTime endDate,
                                             Double minAmount, Double maxAmount, Pageable pageable) {
        log.debug("Fetching orders with filters - userId: {}, status: {}, startDate: {}, endDate: {}, minAmount: {}, maxAmount: {}", 
                 userId, status, startDate, endDate, minAmount, maxAmount);
        Page<Order> orders = orderRepository.findByFilters(userId, status, startDate, endDate, minAmount, maxAmount, pageable);
        return orders.map(this::mapOrderToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByUserId(Long userId, Pageable pageable) {
        log.debug("Fetching orders for user ID: {} with pagination: {}", userId, pageable);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::mapOrderToDto);
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        log.debug("Fetching order by ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        return mapOrderToDto(order);
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order by order number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));
        return mapOrderToDto(order);
    }
    
    public OrderDto createOrder(CreateOrderRequest request) {
        log.info("Creating new order: {}", request.getOrderNumber());
        
        // Check if order number already exists
        if (orderRepository.existsByOrderNumber(request.getOrderNumber())) {
            throw new RuntimeException("Order number already exists: " + request.getOrderNumber());
        }
        
        // Verify user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));
        
        // Create order
        Order order = entityMapper.toOrder(request);
        order.setUser(user);
        
        // Calculate total amount from order items if provided
        if (request.getOrderItems() != null && !request.getOrderItems().isEmpty()) {
            BigDecimal calculatedTotal = request.getOrderItems().stream()
                    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (calculatedTotal.compareTo(request.getTotalAmount()) != 0) {
                log.warn("Calculated total {} does not match provided total {}", calculatedTotal, request.getTotalAmount());
                order.setTotalAmount(calculatedTotal);
            }
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items if provided
        if (request.getOrderItems() != null && !request.getOrderItems().isEmpty()) {
            List<OrderItem> orderItems = entityMapper.toOrderItemList(request.getOrderItems());
            orderItems.forEach(item -> item.setOrder(savedOrder));
            savedOrder.setOrderItems(orderItems);
        }
        
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return mapOrderToDto(savedOrder);
    }
    
    public OrderDto updateOrderStatus(Long id, Order.OrderStatus status) {
        log.info("Updating order status for ID: {} to {}", id, status);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        log.info("Order status updated successfully for ID: {}", id);
        return mapOrderToDto(updatedOrder);
    }
    
    public OrderDto updateOrder(Long id, CreateOrderRequest request) {
        log.info("Updating order with ID: {}", id);
        
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        // Check if order number is being changed and already exists
        if (!existingOrder.getOrderNumber().equals(request.getOrderNumber()) && 
            orderRepository.existsByOrderNumber(request.getOrderNumber())) {
            throw new RuntimeException("Order number already exists: " + request.getOrderNumber());
        }
        
        // Update order fields
        existingOrder.setOrderNumber(request.getOrderNumber());
        existingOrder.setTotalAmount(request.getTotalAmount());
        existingOrder.setCurrency(request.getCurrency());
        existingOrder.setShippingAddress(request.getShippingAddress());
        existingOrder.setBillingAddress(request.getBillingAddress());
        existingOrder.setNotes(request.getNotes());
        
        Order updatedOrder = orderRepository.save(existingOrder);
        
        log.info("Order updated successfully with ID: {}", id);
        return mapOrderToDto(updatedOrder);
    }
    
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);
        
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }
        
        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public long getOrderCountByUserIdAndStatus(Long userId, Order.OrderStatus status) {
        return orderRepository.countByUserIdAndStatus(userId, status);
    }
    
    @Transactional(readOnly = true)
    public Double getTotalAmountByUserIdAndStatus(Long userId, Order.OrderStatus status) {
        Double total = orderRepository.sumTotalAmountByUserIdAndStatus(userId, status);
        return total != null ? total : 0.0;
    }
    
    private OrderDto mapOrderToDto(Order order) {
        OrderDto dto = entityMapper.toOrderDto(order);
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserName(order.getUser().getFullName());
        }
        return dto;
    }
    
    public boolean isOrderOwner(Long orderId, String username) {
        Order order = orderRepository.findById(orderId).orElse(null);
        return order != null && order.getUser() != null && order.getUser().getUsername().equals(username);
    }
    
    public boolean isOrderOwnerByOrderNumber(String orderNumber, String username) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        return order != null && order.getUser() != null && order.getUser().getUsername().equals(username);
    }
}
