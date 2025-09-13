package com.example.aopdemo.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getUserById(int id) {
        return "User with ID: " + id;
    }

    public String createUser(String name) {
        return "Created user: " + name;
    }

    public String generateError() {
        throw new RuntimeException("Simulated error");
    }
}
