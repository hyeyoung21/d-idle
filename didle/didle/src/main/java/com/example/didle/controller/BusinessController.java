package com.example.didle.controller;

import com.example.didle.model.BusinessDTO;
import com.example.didle.model.Business;
import com.example.didle.service.BusinessService;
import com.example.didle.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private final BusinessService businessService;
    private final UserService userService;

    public BusinessController(BusinessService businessService, UserService userService) {
        this.businessService = businessService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerBusiness(@RequestBody BusinessDTO businessDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        try {
            businessDTO.setUserId(userId);
            Business registeredBusiness = businessService.createBusiness(businessDTO);
            userService.changeUserType(userId);

            // 세션 무효화 (로그아웃)
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
    public String showBusinessProfile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Business business = businessService.getBusinessByUserId(userId);
        model.addAttribute("business", business);
        return "business-profile";
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateBusinessProfile(@RequestBody BusinessDTO businessDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        try {
            BusinessDTO updatedBusiness = businessService.updateBusiness(userId, businessDTO);
            return ResponseEntity.ok(updatedBusiness);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update business: " + e.getMessage());
        }
    }
}
