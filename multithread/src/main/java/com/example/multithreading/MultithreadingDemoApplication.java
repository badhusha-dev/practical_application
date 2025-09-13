package com.example.multithreading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MultithreadingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultithreadingDemoApplication.class, args);
    }

}
