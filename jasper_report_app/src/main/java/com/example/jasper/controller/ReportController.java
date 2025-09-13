package com.example.jasper.controller;

import com.example.jasper.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    
    private final ReportService reportService;
    
    @GetMapping("/users")
    public ResponseEntity<byte[]> generateUserReport(
            @RequestParam(defaultValue = "pdf") String format) {
        
        log.info("Received request for user report with format: {}", format);
        
        if (!isValidFormat(format)) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            byte[] reportBytes = reportService.generateUserReport(format);
            
            String contentType = "pdf".equalsIgnoreCase(format) 
                ? MediaType.APPLICATION_PDF_VALUE 
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            
            String filename = "users_report." + format.toLowerCase();
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(reportBytes);
                    
        } catch (Exception e) {
            log.error("Error generating user report", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/orders")
    public ResponseEntity<byte[]> generateOrderReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime to,
            @RequestParam(defaultValue = "pdf") String format) {
        
        log.info("Received request for order report from {} to {} with format: {}", from, to, format);
        
        if (!isValidFormat(format)) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            byte[] reportBytes = reportService.generateOrderReport(format, from, to);
            
            String contentType = "pdf".equalsIgnoreCase(format) 
                ? MediaType.APPLICATION_PDF_VALUE 
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            
            String filename = "orders_report." + format.toLowerCase();
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(reportBytes);
                    
        } catch (Exception e) {
            log.error("Error generating order report", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private boolean isValidFormat(String format) {
        return "pdf".equalsIgnoreCase(format) || "xlsx".equalsIgnoreCase(format);
    }
}

