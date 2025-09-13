package com.concurrency.multithreading.service;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for Deadlock demonstration and resolution
 * Shows how deadlocks occur and how to prevent them
 */
@Slf4j
@Service
public class DeadlockService {
    
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    /**
     * Demonstrates a classic deadlock scenario
     */
    public DemoResult runDeadlockDemo(boolean useFix) {
        log.info("Starting Deadlock demo with fix: {}", useFix);
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "Deadlock Demo",
            "RUNNING",
            useFix ? "Deadlock prevention using ordered locking" : "Classic deadlock scenario"
        );
        
        ExecutorService executor = ThreadUtils.createFixedThreadPool(2);
        
        try {
            if (useFix) {
                // Deadlock prevention - ordered locking
                executor.submit(() -> {
                    synchronized (lock1) {
                        String message = "Thread-1: Acquired lock1";
                        log.info(message);
                        output.add(message);
                        
                        ThreadUtils.simulateWork(100, "Thread-1");
                        
                        synchronized (lock2) {
                            message = "Thread-1: Acquired lock2 (ordered locking)";
                            log.info(message);
                            output.add(message);
                        }
                    }
                });
                
                executor.submit(() -> {
                    synchronized (lock1) { // Same order as Thread-1
                        String message = "Thread-2: Acquired lock1";
                        log.info(message);
                        output.add(message);
                        
                        ThreadUtils.simulateWork(100, "Thread-2");
                        
                        synchronized (lock2) {
                            message = "Thread-2: Acquired lock2 (ordered locking)";
                            log.info(message);
                            output.add(message);
                        }
                    }
                });
            } else {
                // Classic deadlock scenario
                executor.submit(() -> {
                    synchronized (lock1) {
                        String message = "Thread-1: Acquired lock1";
                        log.info(message);
                        output.add(message);
                        
                        ThreadUtils.simulateWork(100, "Thread-1");
                        
                        synchronized (lock2) {
                            message = "Thread-1: Acquired lock2";
                            log.info(message);
                            output.add(message);
                        }
                    }
                });
                
                executor.submit(() -> {
                    synchronized (lock2) { // Different order - causes deadlock
                        String message = "Thread-2: Acquired lock2";
                        log.info(message);
                        output.add(message);
                        
                        ThreadUtils.simulateWork(100, "Thread-2");
                        
                        synchronized (lock1) {
                            message = "Thread-2: Acquired lock1";
                            log.info(message);
                            output.add(message);
                        }
                    }
                });
            }
            
            // Wait for completion or timeout
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                if (!useFix) {
                    result.setStatus("DEADLOCK_DETECTED");
                    output.add("DEADLOCK: Threads are waiting indefinitely for each other's locks");
                    log.warn("Deadlock detected - threads are blocked");
                } else {
                    result.setStatus("COMPLETED");
                    output.add("SUCCESS: No deadlock - ordered locking prevented the issue");
                }
            } else {
                result.setStatus("COMPLETED");
                output.add("SUCCESS: All threads completed successfully");
            }
            
            result.setOutput(output);
            result.setExecutionDurationMs(3000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("Deadlock demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "DeadlockDemo");
        }
        
        log.info("Deadlock demo completed with status: {}", result.getStatus());
        return result;
    }
}
