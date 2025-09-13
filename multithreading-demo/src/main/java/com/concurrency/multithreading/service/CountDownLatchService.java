package com.concurrency.multithreading.service;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for CountDownLatch demonstration
 * Shows how to coordinate multiple threads using CountDownLatch
 */
@Slf4j
@Service
public class CountDownLatchService {
    
    /**
     * Demonstrates CountDownLatch usage for thread coordination
     */
    public DemoResult runCountDownLatchDemo(int workerCount) {
        log.info("Starting CountDownLatch demo with {} workers", workerCount);
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "CountDownLatch Demo",
            "RUNNING",
            "Multiple workers wait for all tasks to complete before proceeding"
        );
        
        CountDownLatch latch = new CountDownLatch(workerCount);
        ExecutorService executor = ThreadUtils.createFixedThreadPool(workerCount + 1);
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        try {
            // Create worker threads
            for (int i = 0; i < workerCount; i++) {
                final int workerId = i;
                executor.submit(() -> {
                    try {
                        String message = String.format("Worker-%d: Starting task", workerId);
                        log.info(message);
                        output.add(message);
                        
                        // Simulate work
                        ThreadUtils.simulateWork(500 + (workerId * 100), "Worker-" + workerId);
                        
                        int completed = completedTasks.incrementAndGet();
                        message = String.format("Worker-%d: Task completed (total completed: %d)", workerId, completed);
                        log.info(message);
                        output.add(message);
                        
                    } finally {
                        latch.countDown();
                        log.debug("Worker-{} counted down latch", workerId);
                    }
                });
            }
            
            // Main thread waits for all workers
            executor.submit(() -> {
                try {
                    String message = "Main Thread: Waiting for all workers to complete...";
                    log.info(message);
                    output.add(message);
                    
                    latch.await();
                    
                    message = String.format("Main Thread: All %d workers completed! Proceeding with final task.", workerCount);
                    log.info(message);
                    output.add(message);
                    
                    // Final task
                    ThreadUtils.simulateWork(200, "Main Thread");
                    
                    message = "Main Thread: Final task completed!";
                    log.info(message);
                    output.add(message);
                    
                } catch (InterruptedException e) {
                    log.error("Main thread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            });
            
            // Wait for completion
            Thread.sleep(2000);
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(2000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("CountDownLatch demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "CountDownLatchDemo");
        }
        
        log.info("CountDownLatch demo completed with {} tasks", completedTasks.get());
        return result;
    }
}
