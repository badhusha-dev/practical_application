package com.example.multithreading.controller;

import com.example.multithreading.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task Controller", description = "Endpoints for demonstrating multithreading approaches")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/async")
    @Operation(
        summary = "Trigger Async Tasks",
        description = "Starts three async tasks (email, report, notification) using Spring's @Async annotation. " +
                     "Returns immediately without waiting for task completion. Tasks run in parallel on different threads."
    )
    public String triggerAsyncTasks() {
        System.out.println("Main Thread: Starting async tasks...");
        
        // Start all tasks asynchronously without waiting for results
        taskService.sendEmail();
        taskService.generateReport();
        taskService.sendNotification();
        
        System.out.println("Main Thread: All async tasks started, returning immediately!");
        return "Tasks started!";
    }

    @GetMapping("/executor")
    @Operation(
        summary = "Trigger Executor Tasks",
        description = "Runs three tasks (email, report, notification) using ExecutorService and CompletableFuture. " +
                     "Waits for all tasks to complete before returning response. Tasks run in parallel using thread pool."
    )
    public String triggerExecutorTasks() {
        System.out.println("Main Thread: Starting executor tasks...");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        try {
            // Submit all tasks to executor
            CompletableFuture<String> emailFuture = CompletableFuture.supplyAsync(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + ": Sending email...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Email sending interrupted";
                }
                System.out.println(threadName + ": Email sent successfully!");
                return "Email sent successfully";
            }, executor);

            CompletableFuture<String> reportFuture = CompletableFuture.supplyAsync(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + ": Generating report...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Report generation interrupted";
                }
                System.out.println(threadName + ": Report generated successfully!");
                return "Report generated successfully";
            }, executor);

            CompletableFuture<String> notificationFuture = CompletableFuture.supplyAsync(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + ": Sending notification...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Notification sending interrupted";
                }
                System.out.println(threadName + ": Notification sent successfully!");
                return "Notification sent successfully";
            }, executor);

            // Wait for all tasks to complete
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                emailFuture, reportFuture, notificationFuture
            );
            
            allTasks.get(10, TimeUnit.SECONDS); // Wait up to 10 seconds
            
            String result = String.format("All tasks completed! Results: %s, %s, %s",
                emailFuture.get(), reportFuture.get(), notificationFuture.get());
            
            System.out.println("Main Thread: All executor tasks completed!");
            return result;
            
        } catch (Exception e) {
            System.err.println("Error executing tasks: " + e.getMessage());
            return "Error: " + e.getMessage();
        } finally {
            executor.shutdown();
        }
    }
}
