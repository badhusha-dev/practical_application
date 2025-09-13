package com.example.apibestpractices.controller;

import com.example.apibestpractices.dto.CreateOrderRequest;
import com.example.apibestpractices.dto.OrderDto;
import com.example.apibestpractices.model.Order;
import com.example.apibestpractices.service.OrderService;
import com.example.apibestpractices.util.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PagedModel<EntityModel<OrderDto>>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount) {
        
        log.info("GET /orders - page: {}, size: {}, sort: {}, userId: {}, status: {}, startDate: {}, endDate: {}, minAmount: {}, maxAmount: {}", 
                page, size, sort, userId, status, startDate, endDate, minAmount, maxAmount);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        
        // Parse status parameter
        Order.OrderStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Order.OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid status: " + status));
            }
        }
        
        Page<OrderDto> ordersPage = orderService.getOrdersWithFilters(
                userId, statusEnum, startDate, endDate, minAmount, maxAmount, pageable);
        
        // Convert to HATEOAS PagedModel
        List<EntityModel<OrderDto>> orderModels = ordersPage.getContent().stream()
                .map(orderDto -> {
                    EntityModel<OrderDto> model = EntityModel.of(orderDto);
                    model.add(linkTo(methodOn(OrderController.class).getOrderById(orderDto.getId())).withSelfRel());
                    model.add(linkTo(methodOn(UserController.class).getUserById(orderDto.getUserId())).withRel("user"));
                    model.add(linkTo(methodOn(OrderController.class).getOrderByOrderNumber(orderDto.getOrderNumber())).withRel("by-order-number"));
                    return model;
                })
                .toList();
        
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                ordersPage.getSize(),
                ordersPage.getNumber(),
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages()
        );
        
        PagedModel<EntityModel<OrderDto>> pagedModel = PagedModel.of(orderModels, pageMetadata);
        
        // Add navigation links
        if (ordersPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(OrderController.class).getAllOrders(
                    page + 1, size, sort, userId, status, startDate, endDate, minAmount, maxAmount)).withRel("next"));
        }
        if (ordersPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(OrderController.class).getAllOrders(
                    page - 1, size, sort, userId, status, startDate, endDate, minAmount, maxAmount)).withRel("prev"));
        }
        
        pagedModel.add(linkTo(OrderController.class).withRel("self"));
        
        return ResponseEntity.ok(ApiResponse.success(pagedModel));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or (hasRole('USER') and @userService.isOwner(#userId, authentication.name))")
    public ResponseEntity<ApiResponse<PagedModel<EntityModel<OrderDto>>>> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {
        
        log.info("GET /orders/user/{} - page: {}, size: {}, sort: {}", userId, page, size, sort);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        
        Page<OrderDto> ordersPage = orderService.getOrdersByUserId(userId, pageable);
        
        // Convert to HATEOAS PagedModel
        List<EntityModel<OrderDto>> orderModels = ordersPage.getContent().stream()
                .map(orderDto -> {
                    EntityModel<OrderDto> model = EntityModel.of(orderDto);
                    model.add(linkTo(methodOn(OrderController.class).getOrderById(orderDto.getId())).withSelfRel());
                    model.add(linkTo(methodOn(UserController.class).getUserById(orderDto.getUserId())).withRel("user"));
                    model.add(linkTo(methodOn(OrderController.class).getOrderByOrderNumber(orderDto.getOrderNumber())).withRel("by-order-number"));
                    return model;
                })
                .toList();
        
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                ordersPage.getSize(),
                ordersPage.getNumber(),
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages()
        );
        
        PagedModel<EntityModel<OrderDto>> pagedModel = PagedModel.of(orderModels, pageMetadata);
        
        // Add navigation links
        if (ordersPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(
                    userId, page + 1, size, sort)).withRel("next"));
        }
        if (ordersPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(
                    userId, page - 1, size, sort)).withRel("prev"));
        }
        
        pagedModel.add(linkTo(methodOn(UserController.class).getUserById(userId)).withRel("user"));
        
        return ResponseEntity.ok(ApiResponse.success(pagedModel));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or (hasRole('USER') and @orderService.isOrderOwner(#id, authentication.name))")
    public ResponseEntity<ApiResponse<EntityModel<OrderDto>>> getOrderById(@PathVariable Long id) {
        log.info("GET /orders/{}", id);
        
        OrderDto order = orderService.getOrderById(id);
        
        EntityModel<OrderDto> model = EntityModel.of(order);
        model.add(linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getUserById(order.getUserId())).withRel("user"));
        model.add(linkTo(methodOn(OrderController.class).getOrderByOrderNumber(order.getOrderNumber())).withRel("by-order-number"));
        model.add(linkTo(OrderController.class).withRel("orders"));
        
        return ResponseEntity.ok(ApiResponse.success(model));
    }
    
    @GetMapping("/order-number/{orderNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or (hasRole('USER') and @orderService.isOrderOwnerByOrderNumber(#orderNumber, authentication.name))")
    public ResponseEntity<ApiResponse<EntityModel<OrderDto>>> getOrderByOrderNumber(@PathVariable String orderNumber) {
        log.info("GET /orders/order-number/{}", orderNumber);
        
        OrderDto order = orderService.getOrderByOrderNumber(orderNumber);
        
        EntityModel<OrderDto> model = EntityModel.of(order);
        model.add(linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getUserById(order.getUserId())).withRel("user"));
        model.add(linkTo(methodOn(OrderController.class).getOrderByOrderNumber(orderNumber)).withRel("by-order-number"));
        model.add(linkTo(OrderController.class).withRel("orders"));
        
        return ResponseEntity.ok(ApiResponse.success(model));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<EntityModel<OrderDto>>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        
        log.info("POST /orders - orderNumber: {}, userId: {}, idempotencyKey: {}", 
                request.getOrderNumber(), request.getUserId(), idempotencyKey);
        
        OrderDto order = orderService.createOrder(request);
        
        EntityModel<OrderDto> model = EntityModel.of(order);
        model.add(linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getUserById(order.getUserId())).withRel("user"));
        model.add(linkTo(methodOn(OrderController.class).getOrderByOrderNumber(order.getOrderNumber())).withRel("by-order-number"));
        model.add(linkTo(OrderController.class).withRel("orders"));
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(model, "Order created successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<EntityModel<OrderDto>>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrderRequest request) {
        
        log.info("PUT /orders/{} - orderNumber: {}", id, request.getOrderNumber());
        
        OrderDto order = orderService.updateOrder(id, request);
        
        EntityModel<OrderDto> model = EntityModel.of(order);
        model.add(linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getUserById(order.getUserId())).withRel("user"));
        model.add(linkTo(methodOn(OrderController.class).getOrderByOrderNumber(order.getOrderNumber())).withRel("by-order-number"));
        model.add(linkTo(OrderController.class).withRel("orders"));
        
        return ResponseEntity.ok(ApiResponse.success(model, "Order updated successfully"));
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<EntityModel<OrderDto>>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        log.info("PATCH /orders/{}/status - status: {}", id, status);
        
        Order.OrderStatus statusEnum;
        try {
            statusEnum = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status: " + status));
        }
        
        OrderDto order = orderService.updateOrderStatus(id, statusEnum);
        
        EntityModel<OrderDto> model = EntityModel.of(order);
        model.add(linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getUserById(order.getUserId())).withRel("user"));
        model.add(linkTo(methodOn(OrderController.class).getOrderByOrderNumber(order.getOrderNumber())).withRel("by-order-number"));
        model.add(linkTo(OrderController.class).withRel("orders"));
        
        return ResponseEntity.ok(ApiResponse.success(model, "Order status updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        log.info("DELETE /orders/{}", id);
        
        orderService.deleteOrder(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Order deleted successfully"));
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<OrderStats>> getOrderStats() {
        log.info("GET /orders/stats");
        
        OrderStats stats = new OrderStats(
                orderService.getOrderCountByUserIdAndStatus(null, Order.OrderStatus.PENDING),
                orderService.getOrderCountByUserIdAndStatus(null, Order.OrderStatus.CONFIRMED),
                orderService.getOrderCountByUserIdAndStatus(null, Order.OrderStatus.SHIPPED),
                orderService.getOrderCountByUserIdAndStatus(null, Order.OrderStatus.DELIVERED)
        );
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @Data
    @AllArgsConstructor
    public static class OrderStats {
        private long pendingOrders;
        private long confirmedOrders;
        private long shippedOrders;
        private long deliveredOrders;
    }
}
