package com.example.didle.controller.vies;

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
        return "user/signup";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }

    @GetMapping("/business")
    public String goToBusinessMain() {
        return "business/b_dashboard";
    }

    @GetMapping("/business/profile")
    public String showBusiness(HttpSession session, Model model) {
        return "business/b_profile";
    }


    @GetMapping("/business/products")
    public String showProducts() {
        return "business/b_products";
    }

    @GetMapping("/business/orders")
    public String showOrders() {
        return "business/b_orders";
    }

    @GetMapping("/admin")
    public String showAdmin() {
        return "admin/a_user";
    }

    @GetMapping("/admin/businesses")
    public String showAdminBusiness() {
        return "admin/a_business";
    }

    @GetMapping("/admin/products")
    public String showAdminProduct() {
        return "admin/a_products";
    }
}

