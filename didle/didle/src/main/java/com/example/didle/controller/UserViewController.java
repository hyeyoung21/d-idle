package com.example.didle.controller;

import com.example.didle.model.Business;
import com.example.didle.service.BusinessService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    private final BusinessService businessService;

    public UserViewController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/business-register")
    public String showBusinessRegistrationForm() {
        return "business-register";
    }

    @GetMapping("/business")
    public String showBusiness(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = session.getAttribute("userType").toString();

        // 사용자가 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (userId == null) {
            return "redirect:/login";
        }

        // 사용자가 BUSINESS 타입이 아닌 경우 접근을 차단
        if (!"BUSINESS".equals(userType)) {
            return "redirect:/";
        }

        try {
            // 현재 사용자의 비즈니스 정보를 조회
            Business business = businessService.getBusinessByUserId(userId);
            model.addAttribute("business", business);
            model.addAttribute("approvalStatus", business.getApproval().getStatus());
            return "business-profile";
        } catch (EntityNotFoundException e) {
            // 비즈니스 정보가 없을 경우 에러 페이지로 이동하거나 메시지 표시
            model.addAttribute("errorMessage", "Business information not found.");
            return "error";
        }
    }


    @GetMapping("/business/products")
    public String showProducts(HttpSession session, Model model) {
        return "b_products";
    }

}

