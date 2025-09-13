package com.concurrency.multithreading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model class representing the result of a concurrency demo execution
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoResult {
    private String demoName;
    private String status;
    private String description;
    private List<String> output;
    private LocalDateTime executionTime;
    private long executionDurationMs;
    private String threadInfo;
    
    public DemoResult(String demoName, String status, String description) {
        this.demoName = demoName;
        this.status = status;
        this.description = description;
        this.executionTime = LocalDateTime.now();
    }
}
