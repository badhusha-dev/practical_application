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
 * Service for Odd/Even number printing demo
 * Demonstrates thread coordination and synchronization
 */
@Slf4j
@Service
public class OddEvenService {
    
    private final Object lock = new Object();
    private volatile boolean isOddTurn = true;
    
    /**
     * Demonstrates printing odd and even numbers using two threads
     * with proper synchronization
     */
    public DemoResult runOddEvenDemo(int maxNumber) {
        log.info("Starting Odd/Even demo with max number: {}", maxNumber);
        
        List<String> output = new ArrayList<>();
        AtomicInteger currentNumber = new AtomicInteger(1);
        
        DemoResult result = new DemoResult(
            "Odd/Even Number Printing",
            "RUNNING",
            "Two threads printing odd and even numbers alternately"
        );
        
        ExecutorService executor = ThreadUtils.createFixedThreadPool(2);
        
        try {
            // Odd number thread
            executor.submit(() -> {
                while (currentNumber.get() <= maxNumber) {
                    synchronized (lock) {
                        while (!isOddTurn && currentNumber.get() <= maxNumber) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        if (currentNumber.get() <= maxNumber && currentNumber.get() % 2 == 1) {
                            String message = String.format("ODD Thread: %d", currentNumber.get());
                            log.info(message);
                            output.add(message);
                            currentNumber.incrementAndGet();
                            isOddTurn = false;
                            lock.notifyAll();
                        }
                    }
                }
            });
            
            // Even number thread
            executor.submit(() -> {
                while (currentNumber.get() <= maxNumber) {
                    synchronized (lock) {
                        while (isOddTurn && currentNumber.get() <= maxNumber) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        if (currentNumber.get() <= maxNumber && currentNumber.get() % 2 == 0) {
                            String message = String.format("EVEN Thread: %d", currentNumber.get());
                            log.info(message);
                            output.add(message);
                            currentNumber.incrementAndGet();
                            isOddTurn = true;
                            lock.notifyAll();
                        }
                    }
                }
            });
            
            // Wait for completion
            Thread.sleep(2000);
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(2000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("Odd/Even demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "OddEvenDemo");
        }
        
        log.info("Odd/Even demo completed with {} output lines", output.size());
        return result;
    }
}
