package com.example.didle.controller.api;

import com.example.didle.model.dto.*;
import com.example.didle.model.vo.Business;
import com.example.didle.service.BusinessService;
import com.example.didle.service.OrderService;
import com.example.didle.service.ProductService;
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
    private final OrderService orderService;
    private final ProductService productService;

    public BusinessController(BusinessService businessService,OrderService orderService, ProductService productService) {
        this.businessService = businessService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerBusiness(@RequestBody BusinessDTO businessDTO) {
        try {
            Business registeredBusiness = businessService.createBusiness(businessDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Business registered successfully. Please log in.");
            response.put("business", registeredBusiness);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to register business: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginBusiness(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String password = credentials.get("passwordHash");

        Business authenticatedBusiness = businessService.authenticateBusiness(username, password);

        if (authenticatedBusiness != null) {
            session.setAttribute("businessId", authenticatedBusiness.getId());
            session.setAttribute("businessName", authenticatedBusiness.getBusinessName());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("businessId", authenticatedBusiness.getId());
            responseBody.put("businessName", authenticatedBusiness.getBusinessName());

            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getBusinessProfile(HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");

        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("business not logged in");
        }

        try {
            Business business = businessService.getBusinessById(businessId);

            // DTO로 변환
            BusinessDTO businessDTO = new BusinessDTO();
            businessDTO.setId(business.getId());
            businessDTO.setUsername(business.getUsername());
            businessDTO.setEmail(business.getEmail());
            businessDTO.setBusinessName(business.getBusinessName());
            businessDTO.setBusinessNumber(business.getBusinessNumber());
            businessDTO.setBusinessAddress(business.getBusinessAddress());
            businessDTO.setBusinessPhone(business.getBusinessPhone());

            // approval이 null이 아닌 경우에만 상태 설정
            if (business.getApproval() != null) {
                businessDTO.setApprovalStatus(business.getApproval().getStatus().toString());
            }

            return ResponseEntity.ok(businessDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Business information not found");
        }
    }


    @PutMapping("/profile")
    public ResponseEntity<?> updateBusinessProfile(@RequestBody BusinessDTO businessDTO, HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            BusinessDTO updatedBusiness = businessService.updateBusiness(businessId, businessDTO);
            return ResponseEntity.ok(updatedBusiness);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update business: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            DashboardDTO dashboard = businessService.getDashboardData(businessId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get dashboard data: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            List<OrderDTO> orders = orderService.getAllOrdersByBusinessId(businessId);
            return ResponseEntity.ok(orders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Business not found");
        }
    }

    @PutMapping("/orders/status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request, HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
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
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        try {
            ProductDTO addedProduct = productService.addProduct(productDTO, image, businessId);
            return ResponseEntity.ok(addedProduct);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Business not found");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add product: " + e.getMessage());
        }
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @ModelAttribute ProductDTO productDTO,
                                           @RequestParam(name = "image", required = false) MultipartFile image,
                                           HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        try {
            ProductDTO updatedProduct = productService.updateProduct(productId, productDTO, image, businessId);
            return ResponseEntity.ok(updatedProduct);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product or business not found");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update product: " + e.getMessage());
        }
    }


}
