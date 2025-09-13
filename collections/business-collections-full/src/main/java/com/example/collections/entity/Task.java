package com.example.collections.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    @Column(name = "status", length = 20)
    private String status = "PENDING";
    
    // Default constructor
    public Task() {}
    
    // Constructor with all fields
    public Task(String description, Integer priority, String status) {
        this.description = description;
        this.priority = priority;
        this.status = status;
    }
    
    // Constructor without status (defaults to PENDING)
    public Task(String description, Integer priority) {
        this.description = description;
        this.priority = priority;
        this.status = "PENDING";
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                '}';
    }
}
