package com.example.multithreading.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {

    @Async("taskExecutor")
    public CompletableFuture<String> sendEmail() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": Sending email...");
        
        try {
            Thread.sleep(2000); // Simulate 2s delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("Email sending interrupted");
        }
        
        System.out.println(threadName + ": Email sent successfully!");
        return CompletableFuture.completedFuture("Email sent successfully");
    }

    @Async("taskExecutor")
    public CompletableFuture<String> generateReport() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": Generating report...");
        
        try {
            Thread.sleep(3000); // Simulate 3s delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("Report generation interrupted");
        }
        
        System.out.println(threadName + ": Report generated successfully!");
        return CompletableFuture.completedFuture("Report generated successfully");
    }

    @Async("taskExecutor")
    public CompletableFuture<String> sendNotification() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": Sending notification...");
        
        try {
            Thread.sleep(1000); // Simulate 1s delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("Notification sending interrupted");
        }
        
        System.out.println(threadName + ": Notification sent successfully!");
        return CompletableFuture.completedFuture("Notification sent successfully");
    }
}
