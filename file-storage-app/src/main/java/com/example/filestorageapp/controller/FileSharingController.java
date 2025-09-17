package com.example.filestorageapp.controller;

import com.example.filestorageapp.entity.SharedFile;
import com.example.filestorageapp.service.FileSharingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/share")
@RequiredArgsConstructor
@Slf4j
public class FileSharingController {

    private final FileSharingService fileSharingService;

    @GetMapping("/{shareToken}")
    public String viewSharedFile(@PathVariable String shareToken, Model model) {
        try {
            SharedFile sharedFile = fileSharingService.getSharedFile(shareToken)
                    .orElseThrow(() -> new RuntimeException("Share not found or expired"));

            if (!sharedFile.canDownload()) {
                model.addAttribute("error", "This share has expired or reached its download limit");
                return "share/error";
            }

            model.addAttribute("sharedFile", sharedFile);
            model.addAttribute("file", sharedFile.getFileMetadata());
            return "share/view";
        } catch (Exception e) {
            log.error("Error viewing shared file", e);
            model.addAttribute("error", e.getMessage());
            return "share/error";
        }
    }

    @GetMapping("/{shareToken}/download")
    public String downloadSharedFile(@PathVariable String shareToken) {
        try {
            String downloadUrl = fileSharingService.getSharedFileDownloadUrl(shareToken);
            return "redirect:" + downloadUrl;
        } catch (Exception e) {
            log.error("Error downloading shared file", e);
            return "redirect:/share/" + shareToken + "?error=" + e.getMessage();
        }
    }
}
