package com.example.didle.repository;

import com.example.didle.model.Category;
import com.example.didle.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);

    List<Product> findByBusinessId(Long businessId);
    Long countByBusinessIdAndStockQuantityGreaterThan(Long businessId, int stockQuantity);
}

