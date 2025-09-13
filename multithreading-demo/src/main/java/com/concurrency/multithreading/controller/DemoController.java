package com.concurrency.multithreading.controller;

import com.concurrency.multithreading.model.DemoResult;
import com.concurrency.multithreading.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling multithreading demo requests
 * Provides endpoints for the Thymeleaf UI
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class DemoController {
    
    private final OddEvenService oddEvenService;
    private final ProducerConsumerService producerConsumerService;
    private final DeadlockService deadlockService;
    private final SingletonService singletonService;
    private final CountDownLatchService countDownLatchService;
    private final CyclicBarrierService cyclicBarrierService;
    private final ReadWriteLockService readWriteLockService;
    private final CompletableFutureService completableFutureService;
    private final ThreadPoolService threadPoolService;
    
    /**
     * Home page with dashboard of all demos
     */
    @GetMapping("/")
    public String home(Model model) {
        log.info("Loading home page");
        model.addAttribute("title", "Multithreading & Concurrency Demos");
        model.addAttribute("description", "Interactive demonstrations of Java multithreading concepts");
        return "index";
    }
    
    /**
     * Run Odd/Even number printing demo
     */
    @PostMapping("/demo/odd-even")
    @ResponseBody
    public DemoResult runOddEvenDemo(@RequestParam(defaultValue = "20") int maxNumber) {
        log.info("Running Odd/Even demo with max number: {}", maxNumber);
        return oddEvenService.runOddEvenDemo(maxNumber);
    }
    
    /**
     * Run Producer-Consumer demo
     */
    @PostMapping("/demo/producer-consumer")
    @ResponseBody
    public DemoResult runProducerConsumerDemo(@RequestParam(defaultValue = "10") int itemCount) {
        log.info("Running Producer-Consumer demo with {} items", itemCount);
        return producerConsumerService.runProducerConsumerDemo(itemCount);
    }
    
    /**
     * Run Deadlock demo
     */
    @PostMapping("/demo/deadlock")
    @ResponseBody
    public DemoResult runDeadlockDemo(@RequestParam(defaultValue = "false") boolean useFix) {
        log.info("Running Deadlock demo with fix: {}", useFix);
        return deadlockService.runDeadlockDemo(useFix);
    }
    
    /**
     * Run Singleton demo
     */
    @PostMapping("/demo/singleton")
    @ResponseBody
    public DemoResult runSingletonDemo(@RequestParam(defaultValue = "5") int threadCount) {
        log.info("Running Singleton demo with {} threads", threadCount);
        return singletonService.runSingletonDemo(threadCount);
    }
    
    /**
     * Run CountDownLatch demo
     */
    @PostMapping("/demo/countdown-latch")
    @ResponseBody
    public DemoResult runCountDownLatchDemo(@RequestParam(defaultValue = "4") int workerCount) {
        log.info("Running CountDownLatch demo with {} workers", workerCount);
        return countDownLatchService.runCountDownLatchDemo(workerCount);
    }
    
    /**
     * Run CyclicBarrier demo
     */
    @PostMapping("/demo/cyclic-barrier")
    @ResponseBody
    public DemoResult runCyclicBarrierDemo(
            @RequestParam(defaultValue = "3") int threadCount,
            @RequestParam(defaultValue = "2") int phases) {
        log.info("Running CyclicBarrier demo with {} threads and {} phases", threadCount, phases);
        return cyclicBarrierService.runCyclicBarrierDemo(threadCount, phases);
    }
    
    /**
     * Run ReadWriteLock demo
     */
    @PostMapping("/demo/readwrite-lock")
    @ResponseBody
    public DemoResult runReadWriteLockDemo(
            @RequestParam(defaultValue = "3") int readerCount,
            @RequestParam(defaultValue = "2") int writerCount) {
        log.info("Running ReadWriteLock demo with {} readers and {} writers", readerCount, writerCount);
        return readWriteLockService.runReadWriteLockDemo(readerCount, writerCount);
    }
    
    /**
     * Run CompletableFuture demo
     */
    @PostMapping("/demo/completable-future")
    @ResponseBody
    public DemoResult runCompletableFutureDemo() {
        log.info("Running CompletableFuture demo");
        return completableFutureService.runCompletableFutureDemo();
    }
    
    /**
     * Run ThreadPool demo
     */
    @PostMapping("/demo/threadpool")
    @ResponseBody
    public DemoResult runThreadPoolDemo(
            @RequestParam(defaultValue = "fixed") String poolType,
            @RequestParam(defaultValue = "6") int taskCount) {
        log.info("Running ThreadPool demo with {} pool and {} tasks", poolType, taskCount);
        return threadPoolService.runThreadPoolDemo(poolType, taskCount);
    }
}
