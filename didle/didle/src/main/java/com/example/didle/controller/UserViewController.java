package com.example.didle.controller;

import com.example.didle.service.BusinessService;
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
    public String goToBusinessMain() {
        return "b_dashboard";
    }

    @GetMapping("/business/profile")
    public String showBusiness(HttpSession session, Model model) {
        return "business-profile";
    }


    @GetMapping("/business/products")
    public String showProducts() {
        return "b_products";
    }

    @GetMapping("/business/orders")
    public String showOrders() {
        return "b_orders";
    }

    @GetMapping("/admin")
    public String showAdmin() {
        return "a_user";
    }

    @GetMapping("/admin/businesses")
    public String showAdminBusiness() {
        return "a_business";
    }

    @GetMapping("/admin/products")
    public String showAdminProduct() {
        return "a_products";
    }
}

