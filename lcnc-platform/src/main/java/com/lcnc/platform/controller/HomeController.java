package com.lcnc.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "LCNC Platform");
        model.addAttribute("description", "Low-Code No-Code Platform with Workflow Engine");
        return "index";
    }
}
