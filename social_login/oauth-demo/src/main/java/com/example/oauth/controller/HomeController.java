package com.example.oauth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/secured")
    public String secured(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String name = principal.getAttribute("name");
            String email = principal.getAttribute("email");
            model.addAttribute("username", name != null ? name : email);
        }
        return "secured";
    }

    @GetMapping("/user")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email"),
                "id", principal.getAttribute("sub"),
                "picture", principal.getAttribute("picture")
            );
        }
        return Map.of("error", "Not authenticated");
    }
}
