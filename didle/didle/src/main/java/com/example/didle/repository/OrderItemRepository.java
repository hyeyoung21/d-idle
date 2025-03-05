package com.example.didle.repository;

import com.example.didle.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);


    @Query("SELECT oi.product.id, SUM(oi.quantity) as total FROM OrderItem oi GROUP BY oi.product.id ORDER BY total DESC")
    List<Object[]> findMostSoldProductId();

    @Modifying
    @Query("UPDATE OrderItem o SET o.product = null WHERE o.product.id = :productId")
    void nullifyProductId(@Param("productId") Long productId);
}

