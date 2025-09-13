package com.concurrency.multithreading.service;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for CyclicBarrier demonstration
 * Shows how to synchronize multiple threads at a common point
 */
@Slf4j
@Service
public class CyclicBarrierService {
    
    /**
     * Demonstrates CyclicBarrier usage for thread synchronization
     */
    public DemoResult runCyclicBarrierDemo(int threadCount, int phases) {
        log.info("Starting CyclicBarrier demo with {} threads and {} phases", threadCount, phases);
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "CyclicBarrier Demo",
            "RUNNING",
            "Multiple threads synchronize at barrier points in multiple phases"
        );
        
        CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
            String message = "=== BARRIER REACHED: All threads synchronized ===";
            log.info(message);
            output.add(message);
        });
        
        ExecutorService executor = ThreadUtils.createFixedThreadPool(threadCount);
        AtomicInteger phaseCounter = new AtomicInteger(0);
        
        try {
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int phase = 0; phase < phases; phase++) {
                            String message = String.format("Thread-%d: Starting phase %d", threadId, phase + 1);
                            log.info(message);
                            output.add(message);
                            
                            // Simulate work
                            ThreadUtils.simulateWork(300 + (threadId * 50), "Thread-" + threadId);
                            
                            message = String.format("Thread-%d: Completed phase %d, waiting at barrier", threadId, phase + 1);
                            log.info(message);
                            output.add(message);
                            
                            // Wait at barrier
                            int currentPhase = barrier.await();
                            
                            message = String.format("Thread-%d: Passed barrier for phase %d", threadId, phase + 1);
                            log.info(message);
                            output.add(message);
                        }
                        
                        String message = String.format("Thread-%d: All phases completed!", threadId);
                        log.info(message);
                        output.add(message);
                        
                    } catch (InterruptedException | BrokenBarrierException e) {
                        log.error("Thread-{} interrupted or barrier broken", threadId, e);
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            // Wait for completion
            Thread.sleep(phases * 1000);
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(phases * 1000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("CyclicBarrier demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "CyclicBarrierDemo");
        }
        
        log.info("CyclicBarrier demo completed with {} phases", phases);
        return result;
    }
}
