package com.concurrency.multithreading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class for shared data in Producer-Consumer demo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedData {
    private int value;
    private boolean available = false;
    private String producer;
    private String consumer;
}
