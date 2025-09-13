package com.concurrency.multithreading.service;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for CompletableFuture demonstration
 * Shows asynchronous programming with CompletableFuture
 */
@Slf4j
@Service
public class CompletableFutureService {
    
    /**
     * Demonstrates CompletableFuture for asynchronous operations
     */
    public DemoResult runCompletableFutureDemo() {
        log.info("Starting CompletableFuture demo");
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "CompletableFuture Async Demo",
            "RUNNING",
            "Demonstrates asynchronous programming with CompletableFuture"
        );
        
        ExecutorService executor = ThreadUtils.createFixedThreadPool(4);
        
        try {
            String message = "Starting asynchronous operations...";
            log.info(message);
            output.add(message);
            
            // Create multiple async tasks
            CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
                ThreadUtils.simulateWork(1000, "Task-1");
                return "Task-1 completed";
            }, executor);
            
            CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
                ThreadUtils.simulateWork(800, "Task-2");
                return "Task-2 completed";
            }, executor);
            
            CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
                ThreadUtils.simulateWork(1200, "Task-3");
                return "Task-3 completed";
            }, executor);
            
            // Chain operations
            CompletableFuture<String> chainedTask = task1.thenApplyAsync(result1 -> {
                String msg = "Chained task processing: " + result1;
                log.info(msg);
                output.add(msg);
                ThreadUtils.simulateWork(500, "ChainedTask");
                return "Chained result: " + result1;
            }, executor);
            
            // Combine results
            CompletableFuture<String> combinedResult = task1.thenCombine(task2, (result1, result2) -> {
                String msg = String.format("Combining results: %s + %s", result1, result2);
                log.info(msg);
                output.add(msg);
                return result1 + " + " + result2;
            });
            
            // Wait for all tasks to complete
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3, chainedTask, combinedResult);
            
            allTasks.thenRun(() -> {
                String msg = "All tasks completed!";
                log.info(msg);
                output.add(msg);
            });
            
            // Get individual results
            task1.thenAccept(result1 -> {
                String msg = "Task-1 result: " + result1;
                log.info(msg);
                output.add(msg);
            });
            
            task2.thenAccept(result2 -> {
                String msg = "Task-2 result: " + result2;
                log.info(msg);
                output.add(msg);
            });
            
            task3.thenAccept(result3 -> {
                String msg = "Task-3 result: " + result3;
                log.info(msg);
                output.add(msg);
            });
            
            chainedTask.thenAccept(chainedResult -> {
                String msg = "Chained task result: " + chainedResult;
                log.info(msg);
                output.add(msg);
            });
            
            combinedResult.thenAccept(combined -> {
                String msg = "Combined result: " + combined;
                log.info(msg);
                output.add(msg);
            });
            
            // Wait for completion
            allTasks.get(3, TimeUnit.SECONDS);
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(3000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (Exception e) {
            log.error("CompletableFuture demo failed", e);
            result.setStatus("FAILED");
            output.add("Error: " + e.getMessage());
            result.setOutput(output);
        } finally {
            ThreadUtils.shutdownExecutor(executor, "CompletableFutureDemo");
        }
        
        log.info("CompletableFuture demo completed with {} output lines", output.size());
        return result;
    }
}
