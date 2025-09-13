package com.concurrency.multithreading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for Multithreading and Concurrency Demos
 * 
 * This application demonstrates various multithreading and concurrency concepts
 * commonly asked in Java interviews, including:
 * - Thread synchronization
 * - Producer-Consumer patterns
 * - Deadlock scenarios and solutions
 * - Thread-safe singletons
 * - Concurrent utilities (CountDownLatch, CyclicBarrier, ReadWriteLock)
 * - CompletableFuture for asynchronous programming
 * - Thread pool management
 */
@SpringBootApplication
public class MultithreadingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultithreadingDemoApplication.class, args);
        System.out.println("\n🚀 Multithreading Demo Application Started!");
        System.out.println("📱 Access the UI at: http://localhost:8080");
        System.out.println("🔧 Check console logs for detailed thread execution details\n");
    }
}
