package com.example.redis.controller;

import com.example.redis.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @PostConstruct
    public void init() {
        // Subscribe to Redis channel on startup
        notificationService.subscribeToMessages();
    }
    
    @GetMapping
    public String notificationsPage(Model model) {
        List<String> messages = notificationService.getMessages();
        model.addAttribute("messages", messages);
        return "notifications";
    }
    
    @PostMapping("/publish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> publishMessage(@RequestParam String message) {
        logger.info("POST /notifications/publish - Publishing message: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            notificationService.publishMessage(message);
            response.put("success", true);
            response.put("message", "Message published successfully");
            
            logger.info("Message published to Redis channel: {}", message);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to publish message: " + e.getMessage());
            
            logger.error("Failed to publish message: {}", message, e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<List<String>> getMessages() {
        logger.info("GET /notifications/messages - Retrieving messages");
        List<String> messages = notificationService.getMessages();
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearMessages() {
        logger.info("POST /notifications/clear - Clearing all messages");
        
        notificationService.clearMessages();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Messages cleared successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/subscribe")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> subscribe() {
        logger.info("GET /notifications/subscribe - Subscribing to Redis channel");
        
        try {
            notificationService.subscribeToMessages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully subscribed to notifications channel");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to subscribe: " + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
}
