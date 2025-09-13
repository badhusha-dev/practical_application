package com.example.thymeleafmongoflywayapp.controller;

import com.example.thymeleafmongoflywayapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    
    private final UserService userService;
    
    @GetMapping("/")
    public String home(Model model) {
        log.info("GET / - Dashboard home");
        
        long userCount = userService.getUserCount();
        model.addAttribute("userCount", userCount);
        
        return "dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("GET /dashboard");
        
        long userCount = userService.getUserCount();
        model.addAttribute("userCount", userCount);
        
        return "dashboard";
    }
}
