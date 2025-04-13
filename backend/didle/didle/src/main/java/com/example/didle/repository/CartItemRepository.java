package com.example.didle.repository;

import com.example.didle.model.vo.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);

    @Transactional
    void deleteByUserId(Long userId);

    @Modifying
    @Query("UPDATE CartItem c SET c.product = null WHERE c.product.id = :productId")
    void nullifyProductId(@Param("productId") Long productId);
}
