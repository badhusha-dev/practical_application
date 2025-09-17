package com.example.filestorageapp.controller;

import com.example.filestorageapp.entity.FileMetadata;
import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.security.CustomUserDetailsService.CustomUserPrincipal;
import com.example.filestorageapp.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserPrincipal userPrincipal, Model model) {
        User user = userPrincipal.getUser();
        
        // Get recent files
        Pageable recentFilesPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FileMetadata> recentFiles = fileService.getUserFiles(user, recentFilesPageable);
        
        model.addAttribute("user", user);
        model.addAttribute("recentFiles", recentFiles.getContent());
        model.addAttribute("totalFiles", recentFiles.getTotalElements());
        model.addAttribute("storageUsagePercentage", user.getStorageUsagePercentage());
        
        return "dashboard";
    }

    @GetMapping("/files")
    public String files(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {
        
        User user = userPrincipal.getUser();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(Sort.Direction.DESC, sortBy) : 
                Sort.by(Sort.Direction.ASC, sortBy);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FileMetadata> files;
        
        if (search != null && !search.trim().isEmpty()) {
            files = fileService.searchUserFiles(user, search, pageable);
            model.addAttribute("search", search);
        } else {
            files = fileService.getUserFiles(user, pageable);
        }
        
        model.addAttribute("files", files);
        model.addAttribute("user", user);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", files.getTotalPages());
        model.addAttribute("totalElements", files.getTotalElements());
        
        return "files/list";
    }

    @PostMapping("/files/upload")
    public String uploadFile(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/files";
        }
        
        try {
            User user = userPrincipal.getUser();
            FileMetadata fileMetadata = fileService.uploadFile(file, user);
            redirectAttributes.addFlashAttribute("success", 
                    "File '" + fileMetadata.getOriginalFilename() + "' uploaded successfully");
        } catch (IOException e) {
            log.error("Error uploading file", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error uploading file", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/files";
    }

    @GetMapping("/files/download/{id}")
    public String downloadFile(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userPrincipal.getUser();
            String downloadUrl = fileService.generateDownloadUrl(id, user);
            return "redirect:" + downloadUrl;
        } catch (Exception e) {
            log.error("Error generating download URL", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/files";
        }
    }

    @PostMapping("/files/delete/{id}")
    public String deleteFile(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userPrincipal.getUser();
            FileMetadata fileMetadata = fileService.getFileById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            
            fileService.deleteFile(id, user);
            redirectAttributes.addFlashAttribute("success", 
                    "File '" + fileMetadata.getOriginalFilename() + "' deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/files";
    }

    @GetMapping("/files/view/{id}")
    public String viewFile(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long id,
            Model model) {
        
        try {
            User user = userPrincipal.getUser();
            FileMetadata fileMetadata = fileService.getFileById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            
            if (!fileMetadata.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
                throw new RuntimeException("Access denied");
            }
            
            model.addAttribute("file", fileMetadata);
            model.addAttribute("user", user);
            
            return "files/view";
        } catch (Exception e) {
            log.error("Error viewing file", e);
            return "redirect:/files";
        }
    }
}
