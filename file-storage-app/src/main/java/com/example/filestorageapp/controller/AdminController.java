package com.example.filestorageapp.controller;

import com.example.filestorageapp.entity.FileMetadata;
import com.example.filestorageapp.entity.SharedFile;
import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.entity.UserRole;
import com.example.filestorageapp.security.CustomUserDetailsService.CustomUserPrincipal;
import com.example.filestorageapp.service.FileService;
import com.example.filestorageapp.service.FileSharingService;
import com.example.filestorageapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final FileService fileService;
    private final FileSharingService fileSharingService;

    @GetMapping
    public String adminDashboard(@AuthenticationPrincipal CustomUserPrincipal userPrincipal, Model model) {
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        User user = userPrincipal.getUser();
        
        // Get statistics
        long totalUsers = userService.getTotalUsers();
        long totalStorageUsed = userService.getTotalStorageUsed();
        long totalStorageQuota = userService.getTotalStorageQuota();
        
        // Get recent files
        Pageable recentFilesPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FileMetadata> recentFiles = fileService.getAllLatestFiles(recentFilesPageable);
        
        // Get active shares
        List<SharedFile> activeShares = fileSharingService.getAllActiveShares();
        
        model.addAttribute("user", user);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalStorageUsed", totalStorageUsed);
        model.addAttribute("totalStorageQuota", totalStorageQuota);
        model.addAttribute("recentFiles", recentFiles.getContent());
        model.addAttribute("activeShares", activeShares);
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {
        
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(Sort.Direction.DESC, sortBy) : 
                Sort.by(Sort.Direction.ASC, sortBy);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users;
        
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search, pageable);
            model.addAttribute("search", search);
        } else {
            users = userService.getAllUsers(pageable);
        }
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalElements", users.getTotalElements());
        
        return "admin/users";
    }

    @PostMapping("/users/{userId}/toggle-status")
    public String toggleUserStatus(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long userId,
            RedirectAttributes redirectAttributes) {
        
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        try {
            User user = userService.toggleUserStatus(userId);
            redirectAttributes.addFlashAttribute("success", 
                    "User " + user.getUsername() + " status updated");
        } catch (Exception e) {
            log.error("Error toggling user status", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/change-role")
    public String changeUserRole(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long userId,
            @RequestParam UserRole newRole,
            RedirectAttributes redirectAttributes) {
        
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        try {
            User user = userService.changeUserRole(userId, newRole);
            redirectAttributes.addFlashAttribute("success", 
                    "User " + user.getUsername() + " role changed to " + newRole);
        } catch (Exception e) {
            log.error("Error changing user role", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/update-quota")
    public String updateStorageQuota(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long userId,
            @RequestParam Long newQuota,
            RedirectAttributes redirectAttributes) {
        
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        try {
            User user = userService.updateUserStorageQuota(userId, newQuota);
            redirectAttributes.addFlashAttribute("success", 
                    "Storage quota updated for " + user.getUsername());
        } catch (Exception e) {
            log.error("Error updating storage quota", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @GetMapping("/files")
    public String manageFiles(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {
        
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(Sort.Direction.DESC, sortBy) : 
                Sort.by(Sort.Direction.ASC, sortBy);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FileMetadata> files;
        
        if (search != null && !search.trim().isEmpty()) {
            files = fileService.getAllLatestFilesBySearch(search, pageable);
            model.addAttribute("search", search);
        } else {
            files = fileService.getAllLatestFiles(pageable);
        }
        
        model.addAttribute("files", files);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", files.getTotalPages());
        model.addAttribute("totalElements", files.getTotalElements());
        
        return "admin/files";
    }

    @GetMapping("/shares")
    public String manageShares(@AuthenticationPrincipal CustomUserPrincipal userPrincipal, Model model) {
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        List<SharedFile> activeShares = fileSharingService.getAllActiveShares();
        model.addAttribute("shares", activeShares);
        
        return "admin/shares";
    }

    @PostMapping("/shares/{shareId}/revoke")
    public String revokeShare(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long shareId,
            RedirectAttributes redirectAttributes) {
        
        if (!userPrincipal.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        try {
            User user = userPrincipal.getUser();
            fileSharingService.revokeShare(shareId, user);
            redirectAttributes.addFlashAttribute("success", "Share revoked successfully");
        } catch (Exception e) {
            log.error("Error revoking share", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/shares";
    }
}
