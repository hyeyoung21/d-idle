package com.example.didle.repository;

import com.example.didle.model.vo.Category;
import com.example.didle.model.vo.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByCategory(Category category);

    List<Product> findByBusinessId(Long businessId);
    Long countByBusinessIdAndStockQuantityGreaterThan(Long businessId, int stockQuantity);

    List<Product> findByCategoryId(Long categoryId);
}

