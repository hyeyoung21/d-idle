package com.example.didle.controller.api;

import com.example.didle.model.dto.BusinessDTO;
import com.example.didle.model.dto.ProductDTO;
import com.example.didle.model.dto.UserDTO;
import com.example.didle.service.AdminProductService;
import com.example.didle.service.BusinessService;
import com.example.didle.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final BusinessService businessService;
    private final AdminProductService productService;

    public AdminController(UserService userService, BusinessService businessService, AdminProductService productService) {
        this.userService = userService;
        this.businessService = businessService;
        this.productService = productService;
    }

    // 회원 목록 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 회원 삭제
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to delete user"));
        }
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessDTO>> getAllBusinesses() {
        List<BusinessDTO> businesses = businessService.getAllBusinesses();
        return ResponseEntity.ok(businesses);
    }

    @PutMapping("/businesses/{id}/approval")
    public ResponseEntity<?> updateBusinessApproval(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        try {
            BusinessDTO updatedBusiness = businessService.updateBusinessApproval(id, status);
            return ResponseEntity.ok(updatedBusiness);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<?> deleteBusiness(@PathVariable Long id) {
        try {
            businessService.deleteBusiness(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 물품 목록 조회
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 물품 추가
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.addProduct(productDTO);
        return ResponseEntity.ok(createdProduct);
    }

    // 물품 삭제
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product not found"));
        }
    }
}
