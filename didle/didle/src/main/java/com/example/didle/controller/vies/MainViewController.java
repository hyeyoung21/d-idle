package com.example.didle.controller.vies;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    @GetMapping("/")
    public String home() {
        return "user/index";
    }

    @GetMapping("/products")
    public String products() {
        return "user/products";
    }

    @GetMapping("/cart")
    public String cart() {
        return "user/cart";
    }

    @GetMapping("/orders")
    public String orders() {
        return "user/orders";
    }
}

