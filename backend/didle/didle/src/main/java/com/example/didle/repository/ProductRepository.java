package com.example.didle.repository;

import com.example.didle.model.vo.Category;
import com.example.didle.model.vo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByCategory(Category category);

    List<Product> findByBusinessId(Long businessId);
    Long countByBusinessIdAndStockQuantityGreaterThan(Long businessId, int stockQuantity);

    List<Product> findByCategoryId(Long categoryId);


    // 특정 카테고리의 상품을 페이징 처리하여 가져오기
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // 가장 많이 팔린 상품 조회 (예: OrderItem 테이블과 조인 필요)
    @Query("SELECT p FROM Product p WHERE p.id = (SELECT oi.product.id FROM OrderItem oi GROUP BY oi.product.id ORDER BY COUNT(oi.product.id) DESC LIMIT 1)")
    Optional<Product> findMostSoldProduct();

}

