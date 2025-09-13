package com.concurrency.multithreading.service;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.model.SharedData;
import com.concurrency.multithreading.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Service for Producer-Consumer demo using wait() and notify()
 * Demonstrates inter-thread communication and synchronization
 */
@Slf4j
@Service
public class ProducerConsumerService {
    
    private final Object lock = new Object();
    private SharedData sharedData = new SharedData();
    
    /**
     * Demonstrates Producer-Consumer pattern using wait() and notify()
     */
    public DemoResult runProducerConsumerDemo(int itemCount) {
        log.info("Starting Producer-Consumer demo with {} items", itemCount);
        
        List<String> output = new ArrayList<>();
        DemoResult result = new DemoResult(
            "Producer-Consumer with wait() & notify()",
            "RUNNING",
            "Producer produces items, Consumer consumes them using wait/notify"
        );
        
        ExecutorService executor = ThreadUtils.createFixedThreadPool(2);
        
        try {
            // Producer thread
            executor.submit(() -> {
                for (int i = 1; i <= itemCount; i++) {
                    synchronized (lock) {
                        while (sharedData.isAvailable()) {
                            try {
                                log.debug("Producer waiting - buffer full");
                                lock.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        sharedData.setValue(i);
                        sharedData.setAvailable(true);
                        sharedData.setProducer(Thread.currentThread().getName());
                        
                        String message = String.format("PRODUCER: Produced item %d", i);
                        log.info(message);
                        output.add(message);
                        
                        lock.notifyAll();
                    }
                    
                    // Simulate production time
                    ThreadUtils.simulateWork(100, "Producer");
                }
            });
            
            // Consumer thread
            executor.submit(() -> {
                for (int i = 1; i <= itemCount; i++) {
                    synchronized (lock) {
                        while (!sharedData.isAvailable()) {
                            try {
                                log.debug("Consumer waiting - buffer empty");
                                lock.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        int value = sharedData.getValue();
                        sharedData.setAvailable(false);
                        sharedData.setConsumer(Thread.currentThread().getName());
                        
                        String message = String.format("CONSUMER: Consumed item %d", value);
                        log.info(message);
                        output.add(message);
                        
                        lock.notifyAll();
                    }
                    
                    // Simulate consumption time
                    ThreadUtils.simulateWork(150, "Consumer");
                }
            });
            
            // Wait for completion
            Thread.sleep(itemCount * 300);
            
            result.setStatus("COMPLETED");
            result.setOutput(output);
            result.setExecutionDurationMs(itemCount * 300);
            result.setThreadInfo(ThreadUtils.getCurrentThreadInfo());
            
        } catch (InterruptedException e) {
            log.error("Producer-Consumer demo interrupted", e);
            result.setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        } finally {
            ThreadUtils.shutdownExecutor(executor, "ProducerConsumerDemo");
        }
        
        log.info("Producer-Consumer demo completed with {} output lines", output.size());
        return result;
    }
}
