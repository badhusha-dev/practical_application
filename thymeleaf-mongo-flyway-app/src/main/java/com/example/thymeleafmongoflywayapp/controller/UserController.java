package com.example.thymeleafmongoflywayapp.controller;

import com.example.thymeleafmongoflywayapp.entity.User;
import com.example.thymeleafmongoflywayapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {
        
        log.info("GET /users - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", 
                page, size, sortBy, sortDir, search);
        
        Page<User> usersPage;
        
        if (search != null && !search.trim().isEmpty()) {
            usersPage = userService.searchUsers(search.trim(), page, size, sortBy, sortDir);
            model.addAttribute("search", search);
        } else {
            usersPage = userService.getAllUsersWithPagination(page, size, sortBy, sortDir);
        }
        
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "users/list";
    }
    
    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        log.info("GET /users/new");
        model.addAttribute("user", new User());
        model.addAttribute("isEdit", false);
        return "users/form";
    }
    
    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, 
                          BindingResult result, 
                          RedirectAttributes redirectAttributes) {
        
        log.info("POST /users/save - email: {}", user.getEmail());
        
        if (result.hasErrors()) {
            log.warn("Validation errors for user: {}", result.getAllErrors());
            return "users/form";
        }
        
        // Check if email already exists
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "users/form";
        }
        
        try {
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            log.info("User created successfully: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
        }
        
        return "redirect:/users";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable String id, Model model) {
        log.info("GET /users/edit/{}", id);
        
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            log.warn("User not found with ID: {}", id);
            return "redirect:/users";
        }
        
        model.addAttribute("user", user.get());
        model.addAttribute("isEdit", true);
        return "users/form";
    }
    
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable String id,
                           @Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        
        log.info("POST /users/update/{} - email: {}", id, user.getEmail());
        
        if (result.hasErrors()) {
            log.warn("Validation errors for user update: {}", result.getAllErrors());
            return "users/form";
        }
        
        // Check if email exists for another user
        if (userService.emailExistsForOtherUser(user.getEmail(), id)) {
            result.rejectValue("email", "error.user", "Email already exists for another user");
            return "users/form";
        }
        
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            log.info("User updated successfully: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
        }
        
        return "redirect:/users";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        log.info("GET /users/delete/{}", id);
        
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
            log.info("User deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        
        return "redirect:/users";
    }
    
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable String id, Model model) {
        log.info("GET /users/view/{}", id);
        
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            log.warn("User not found with ID: {}", id);
            return "redirect:/users";
        }
        
        model.addAttribute("user", user.get());
        return "users/view";
    }
}
