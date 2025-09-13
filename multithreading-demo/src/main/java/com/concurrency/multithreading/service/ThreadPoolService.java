package com.concurrency.multithreading.service;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for ThreadPool demonstration
 * Shows different thread pool configurations and their behavior
 */
@Slf4j
@Service
public class ThreadPoolService {
    
    /**
     * Demonstrates different thread pool configurations
     */
    public DemoResult runThreadPoolDemo(String poolType, int taskCount) {
        log.info("Starting ThreadPool demo with {} pool and {} tasks", poolType, taskCount);
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "ThreadPool Demo",
            "RUNNING",
            "Demonstrates different thread pool configurations: " + poolType
        );
        
        ExecutorService executor;
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        // Create appropriate thread pool based on type
        switch (poolType.toLowerCase()) {
            case "fixed":
                executor = ThreadUtils.createFixedThreadPool(3);
                output.add("Created Fixed Thread Pool with 3 threads");
                break;
            case "cached":
                executor = ThreadUtils.createCachedThreadPool();
                output.add("Created Cached Thread Pool");
                break;
            case "single":
                executor = ThreadUtils.createSingleThreadExecutor();
                output.add("Created Single Thread Executor");
                break;
            default:
                executor = ThreadUtils.createFixedThreadPool(2);
                output.add("Created Default Fixed Thread Pool with 2 threads");
        }
        
        try {
            // Submit tasks
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    String message = String.format("Task-%d: Started by %s", taskId, Thread.currentThread().getName());
                    log.info(message);
                    output.add(message);
                    
                    // Simulate work with varying duration
                    int workTime = 200 + (taskId * 100);
                    ThreadUtils.simulateWork(workTime, "Task-" + taskId);
                    
                    int completed = completedTasks.incrementAndGet();
                    message = String.format("Task-%d: Completed by %s (total completed: %d)", 
                        taskId, Thread.currentThread().getName(), completed);
                    log.info(message);
                    output.add(message);
                });
            }
            
            // Add thread pool statistics
            output.add(ThreadUtils.getThreadPoolStats(executor));
            
            // Wait for completion
            Thread.sleep(3000);
            
            // Final statistics
            output.add(ThreadUtils.getThreadPoolStats(executor));
            output.add(String.format("Total tasks completed: %d/%d", completedTasks.get(), taskCount));
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(3000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("ThreadPool demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "ThreadPoolDemo");
        }
        
        log.info("ThreadPool demo completed with {} tasks", completedTasks.get());
        return result;
    }
}
