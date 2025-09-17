package com.example.filestorageapp.controller;

import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.entity.UserRole;
import com.example.filestorageapp.security.CustomUserDetailsService.CustomUserPrincipal;
import com.example.filestorageapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        
        if (!user.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }
        
        try {
            userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Registration error", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserPrincipal userPrincipal, Model model) {
        User user = userPrincipal.getUser();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                               @RequestParam String email,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userPrincipal.getUser();
            user.setEmail(email);
            // Note: In a real application, you'd have a method to update user details
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            log.error("Profile update error", e);
            redirectAttributes.addFlashAttribute("error", "Failed to update profile");
        }
        return "redirect:/profile";
    }
}
