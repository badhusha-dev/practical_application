package com.example.collections.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    // Default constructor
    public Task() {}
    
    // Constructor with fields
    public Task(String description, Integer priority) {
        this.description = description;
        this.priority = priority;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && 
               Objects.equals(description, task.description) && 
               Objects.equals(priority, task.priority);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, description, priority);
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                '}';
    }
}
