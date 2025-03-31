package com.example.didle.controller.api;

import com.example.didle.model.dto.BusinessDTO;
import com.example.didle.model.dto.DashboardDTO;
import com.example.didle.model.dto.OrderDTO;
import com.example.didle.model.dto.ProductDTO;
import com.example.didle.model.vo.Business;
import com.example.didle.model.vo.UpdateOrderStatusRequest;
import com.example.didle.service.BusinessService;
import com.example.didle.service.OrderService;
import com.example.didle.service.ProductService;
import com.example.didle.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private final BusinessService businessService;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    public BusinessController(BusinessService businessService, UserService userService, OrderService orderService, ProductService productService) {
        this.businessService = businessService;
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerBusiness(@RequestBody BusinessDTO businessDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            businessDTO.setUserId(userId);
            Business registeredBusiness = businessService.createBusiness(businessDTO);
            userService.changeUserType(userId);

            session.invalidate();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Business registered successfully. Please log in again.");
            response.put("business", registeredBusiness);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to register business: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getBusinessProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            BusinessDTO businessDTO = businessService.getBusinessDTOByUserId(userId);
            return ResponseEntity.ok(businessDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Business information not found");
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateBusinessProfile(@RequestBody BusinessDTO businessDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            BusinessDTO updatedBusiness = businessService.updateBusiness(userId, businessDTO);
            return ResponseEntity.ok(updatedBusiness);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update business: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            DashboardDTO dashboard = businessService.getDashboardData(userId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get dashboard data: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            Long businessId = businessService.getBusinessByUserId(userId).getId();
            List<OrderDTO> orders = orderService.getAllOrdersByBusinessId(businessId);
            return ResponseEntity.ok(orders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Business not found");
        }
    }

    @PutMapping("/orders/status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            Long businessId = businessService.getBusinessByUserId(userId).getId();
            OrderDTO updatedOrder = orderService.updateOrderStatus(request.getOrderId(), request.getStatus(), businessId);
            return ResponseEntity.ok(updatedOrder);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Business or Order not found");
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@ModelAttribute ProductDTO productDTO,
                                        @RequestParam("image") MultipartFile image,
                                        HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        try {
            Business business = businessService.getBusinessByUserId(userId);
            ProductDTO addedProduct = productService.addProduct(productDTO, image, business.getId());
            return ResponseEntity.ok(addedProduct);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Business not found");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add product: " + e.getMessage());
        }
    }

}
