package com.example.didle.repository;

import com.example.didle.model.vo.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("SELECT SUM(oi.price * oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE oi.product.businessId = :businessId " +
            "AND o.status <> 'CANCELLED'")
    BigDecimal sumTotalPriceByBusinessId(@Param("businessId") Long businessId);


    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderItems oi WHERE oi.product.businessId = :businessId")
    Long countByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.product.businessId = :businessId ORDER BY o.createdAt DESC")
    List<Order> findTop5ByBusinessIdOrderByCreatedAtDesc(@Param("businessId") Long businessId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.product.businessId = :businessId")
    List<Order> findByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE o.id = :id AND oi.product.businessId = :businessId")
    Optional<Order> findByIdAndBusinessId(@Param("id") Long id, @Param("businessId") Long businessId);

}
