package com.concurrency.multithreading.service;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for ReadWriteLock demonstration
 * Shows how to optimize concurrent access with read/write locks
 */
@Slf4j
@Service
public class ReadWriteLockService {
    
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int sharedData = 0;
    private final AtomicInteger readCount = new AtomicInteger(0);
    private final AtomicInteger writeCount = new AtomicInteger(0);
    
    /**
     * Demonstrates ReadWriteLock usage for concurrent read/write operations
     */
    public DemoResult runReadWriteLockDemo(int readerCount, int writerCount) {
        log.info("Starting ReadWriteLock demo with {} readers and {} writers", readerCount, writerCount);
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "ReadWriteLock Demo",
            "RUNNING",
            "Multiple readers can access data concurrently, writers get exclusive access"
        );
        
        ExecutorService executor = ThreadUtils.createFixedThreadPool(readerCount + writerCount);
        
        try {
            // Create reader threads
            for (int i = 0; i < readerCount; i++) {
                final int readerId = i;
                executor.submit(() -> {
                    for (int j = 0; j < 3; j++) {
                        lock.readLock().lock();
                        try {
                            int value = sharedData;
                            int count = readCount.incrementAndGet();
                            String message = String.format("Reader-%d: Read value %d (read count: %d)", readerId, value, count);
                            log.info(message);
                            output.add(message);
                            
                            // Simulate reading time
                            ThreadUtils.simulateWork(100, "Reader-" + readerId);
                            
                        } finally {
                            lock.readLock().unlock();
                        }
                    }
                });
            }
            
            // Create writer threads
            for (int i = 0; i < writerCount; i++) {
                final int writerId = i;
                executor.submit(() -> {
                    for (int j = 0; j < 2; j++) {
                        lock.writeLock().lock();
                        try {
                            sharedData++;
                            int count = writeCount.incrementAndGet();
                            String message = String.format("Writer-%d: Wrote value %d (write count: %d)", writerId, sharedData, count);
                            log.info(message);
                            output.add(message);
                            
                            // Simulate writing time
                            ThreadUtils.simulateWork(200, "Writer-" + writerId);
                            
                        } finally {
                            lock.writeLock().unlock();
                        }
                    }
                });
            }
            
            // Wait for completion
            Thread.sleep(2000);
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(2000);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("ReadWriteLock demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "ReadWriteLockDemo");
        }
        
        log.info("ReadWriteLock demo completed - Final value: {}, Reads: {}, Writes: {}", 
            sharedData, readCount.get(), writeCount.get());
        return result;
    }
}
