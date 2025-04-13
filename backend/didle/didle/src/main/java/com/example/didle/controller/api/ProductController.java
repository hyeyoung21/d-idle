package com.example.didle.controller.api;

import com.example.didle.model.dto.ProductDTO;
import com.example.didle.model.vo.Product;
import com.example.didle.service.BusinessService;
import com.example.didle.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final BusinessService businessService;

    public ProductController(ProductService productService, BusinessService businessService) {
        this.productService = productService;
        this.businessService = businessService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        List<ProductDTO> products = productService.getAllProducts(search, categoryId);
        return ResponseEntity.ok(products);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }
        return dto;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/seller")
    public ResponseEntity<List<ProductDTO>> getSellerProducts(HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ProductDTO> products = productService.getProductsByBusinessId(businessId);
        return ResponseEntity.ok(products);
    }

    // 가장 많이 팔린 상품 조회
    @GetMapping("/most-sold")
    public ResponseEntity<ProductDTO> getMostSoldProduct() {
        ProductDTO mostSoldProduct = productService.getMostSoldProduct();
        return ResponseEntity.ok(mostSoldProduct);
    }

    // 특정 카테고리의 상위 N개 상품 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "3") int limit) {
        List<ProductDTO> products = productService.getTopProductsByCategory(categoryId, limit);
        return ResponseEntity.ok(products);
    }

}

