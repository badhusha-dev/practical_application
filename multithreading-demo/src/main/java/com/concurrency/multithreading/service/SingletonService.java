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
 * Service for Thread-safe Singleton demonstration
 * Shows different approaches to implementing thread-safe singletons
 */
@Slf4j
@Service
public class SingletonService {
    
    private static volatile SingletonService instance;
    private static final Object lock = new Object();
    private final AtomicInteger accessCount = new AtomicInteger(0);
    
    /**
     * Thread-safe singleton using double-checked locking
     */
    public static SingletonService getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SingletonService();
                }
            }
        }
        return instance;
    }
    
    private SingletonService() {
        log.info("Singleton instance created");
    }
    
    /**
     * Demonstrates thread-safe singleton access
     */
    public DemoResult runSingletonDemo(int threadCount) {
        log.info("Starting Singleton demo with {} threads", threadCount);
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "Thread-safe Singleton",
            "RUNNING",
            "Multiple threads accessing singleton instance safely"
        );
        
        ExecutorService executor = ThreadUtils.createFixedThreadPool(threadCount);
        
        try {
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    SingletonService singleton = SingletonService.getInstance();
                    int count = singleton.accessCount.incrementAndGet();
                    
                    String message = String.format("Thread-%d: Accessed singleton (count: %d, instance: %s)",
                        threadId, count, singleton.hashCode());
                    log.info(message);
                    output.add(message);
                    
                    // Simulate some work
                    ThreadUtils.simulateWork(50, "Thread-" + threadId);
                });
            }
            
            // Wait for completion
            Thread.sleep(1000);
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(1000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("Singleton demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "SingletonDemo");
        }
        
        log.info("Singleton demo completed with {} accesses", accessCount.get());
        return result;
    }
    
    public int getAccessCount() {
        return accessCount.get();
    }
}
