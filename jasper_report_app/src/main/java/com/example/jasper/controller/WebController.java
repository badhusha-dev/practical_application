package com.example.jasper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {
    
    @GetMapping
    public String home() {
        return "index";
    }
    
    @GetMapping("/reports/users/page")
    public String usersReportPage() {
        return "reports/users";
    }
    
    @GetMapping("/reports/orders/page")
    public String ordersReportPage() {
        return "reports/orders";
    }
}
