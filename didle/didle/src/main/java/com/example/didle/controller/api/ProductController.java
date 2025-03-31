package com.example.didle.controller.api;

import com.example.didle.model.vo.Business;
import com.example.didle.model.vo.Product;
import com.example.didle.model.dto.ProductDTO;
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



    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Business business = businessService.getBusinessByUserId(userId);
            if (business == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null || !existingProduct.getBusinessId().equals(business.getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Update existing product with new data
            existingProduct.setName(productDTO.getName());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setStockQuantity(productDTO.getStockQuantity());

            Product updatedProduct = productService.updateProduct(existingProduct, productDTO.getCategoryId());
            return ResponseEntity.ok(convertToDTO(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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


    @GetMapping("/most-sold")
    public ResponseEntity<?> getMostSoldProduct() {
        try {
            Product mostSoldProduct = productService.getMostSoldProduct();
            return ResponseEntity.ok(mostSoldProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found");
        }
    }

    @GetMapping("/seller")
    public ResponseEntity<List<ProductDTO>> getSellerProducts(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Business business = businessService.getBusinessByUserId(userId);
        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ProductDTO> products = productService.getProductsByBusinessId(business.getId());
        return ResponseEntity.ok(products);
    }

}

