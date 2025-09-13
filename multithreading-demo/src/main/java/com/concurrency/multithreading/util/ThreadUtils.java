package com.concurrency.multithreading.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for thread management and common concurrency operations
 */
@Slf4j
@UtilityClass
public class ThreadUtils {
    
    /**
     * Creates a fixed thread pool with the specified number of threads
     */
    public static ExecutorService createFixedThreadPool(int threadCount) {
        log.info("Creating fixed thread pool with {} threads", threadCount);
        return Executors.newFixedThreadPool(threadCount);
    }
    
    /**
     * Creates a cached thread pool
     */
    public static ExecutorService createCachedThreadPool() {
        log.info("Creating cached thread pool");
        return Executors.newCachedThreadPool();
    }
    
    /**
     * Creates a single thread executor
     */
    public static ExecutorService createSingleThreadExecutor() {
        log.info("Creating single thread executor");
        return Executors.newSingleThreadExecutor();
    }
    
    /**
     * Safely shuts down an executor service
     */
    public static void shutdownExecutor(ExecutorService executor, String name) {
        log.info("Shutting down executor: {}", name);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Executor {} did not terminate gracefully, forcing shutdown", name);
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("Executor {} did not terminate after forced shutdown", name);
                }
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while shutting down executor: {}", name, e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Gets thread pool statistics
     */
    public static String getThreadPoolStats(ExecutorService executor) {
        if (executor instanceof ThreadPoolExecutor tpe) {
            return String.format(
                "Thread Pool Stats - Active: %d, Pool Size: %d, Core Pool Size: %d, " +
                "Max Pool Size: %d, Queue Size: %d, Completed Tasks: %d",
                tpe.getActiveCount(),
                tpe.getPoolSize(),
                tpe.getCorePoolSize(),
                tpe.getMaximumPoolSize(),
                tpe.getQueue().size(),
                tpe.getCompletedTaskCount()
            );
        }
        return "Thread pool stats not available";
    }
    
    /**
     * Simulates some work with a delay
     */
    public static void simulateWork(int delayMs, String taskName) {
        try {
            log.debug("{} starting work for {}ms", taskName, delayMs);
            Thread.sleep(delayMs);
            log.debug("{} completed work", taskName);
        } catch (InterruptedException e) {
            log.warn("{} interrupted", taskName, e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Gets current thread information
     */
    public static String getCurrentThreadInfo() {
        Thread currentThread = Thread.currentThread();
        return String.format("Thread: %s (ID: %d, Priority: %d, State: %s)",
            currentThread.getName(),
            currentThread.getId(),
            currentThread.getPriority(),
            currentThread.getState()
        );
    }
}
