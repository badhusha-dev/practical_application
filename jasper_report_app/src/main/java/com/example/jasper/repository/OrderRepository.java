package com.example.jasper.repository;

import com.example.jasper.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.product p " +
           "WHERE o.orderDate BETWEEN :fromDate AND :toDate " +
           "ORDER BY o.orderDate DESC")
    List<Order> findOrdersWithDetailsByDateRange(@Param("fromDate") LocalDateTime fromDate, 
                                                 @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.product p " +
           "ORDER BY o.orderDate DESC")
    List<Order> findAllOrdersWithDetails();
}

