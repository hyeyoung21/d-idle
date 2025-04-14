package com.example.didle.controller.api;

import com.example.didle.model.dto.*;
import com.example.didle.model.vo.Business;
import com.example.didle.service.BusinessService;
import com.example.didle.service.OrderService;
import com.example.didle.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
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
        String password = credentials.get("passwordHash"); // 실제로는 해시 비교 필요

        Business authenticatedBusiness = businessService.authenticateBusiness(username, password);

        if (authenticatedBusiness != null) {
            session.setAttribute("businessId", authenticatedBusiness.getId());
            session.setAttribute("businessName", authenticatedBusiness.getBusinessName()); // 세션에 businessName 저장

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("businessId", authenticatedBusiness.getId());
            responseBody.put("businessName", authenticatedBusiness.getBusinessName());
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }

    // --- !!! 현재 로그인된 비즈니스 정보 반환 엔드포인트 추가 !!! ---
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentBusiness(HttpSession session) {
        Long businessId = (session != null) ? (Long) session.getAttribute("businessId") : null;
        String businessName = (session != null) ? (String) session.getAttribute("businessName") : null;

        if (businessId == null || businessName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 비로그인 시 401
        }

        // JavaScript에서 필요한 정보만 반환 (예: businessName)
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("businessName", businessName);
        // 필요하다면 다른 정보 (role 등) 추가 가능
        // responseBody.put("role", "BUSINESS"); // 예시

        return ResponseEntity.ok(responseBody);
    }

    // --- !!! 로그아웃 엔드포인트 (사용자 로그아웃과 동일하게 처리 가능) !!! ---
    // 별도 엔드포인트 또는 공통 /api/logout 사용 가능. 여기서는 별도 생성 예시
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutBusiness(HttpServletRequest request, HttpServletResponse response, Authentication authentication, HttpSession session) {
        if (session != null) {
            session.invalidate(); // 세션 무효화
        } else {
            System.out.println("Business logout attempt with no active session.");
        }

        // Spring Security Logout Handler 사용 (선택 사항, 더 안전)
        // if (authentication != null) {
        //     new SecurityContextLogoutHandler().logout(request, response, authentication);
        // }

        return ResponseEntity.noContent().build(); // 204 No Content
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

    @PutMapping("/products/{currentProductId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long currentProductId,
                                           @ModelAttribute ProductDTO productDTO,
                                           @RequestParam(name = "image", required = false) MultipartFile image,
                                           HttpSession session) {
        Long businessId = (Long) session.getAttribute("businessId");
        if (businessId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        try {
            ProductDTO updatedProduct = productService.updateProduct(currentProductId, productDTO, image, businessId);
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
