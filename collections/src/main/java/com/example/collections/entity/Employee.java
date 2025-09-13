package com.example.collections.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "department_id")
    private Integer departmentId;
    
    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;
    
    // Default constructor
    public Employee() {}
    
    // Constructor with fields
    public Employee(String name, Integer departmentId, String skills) {
        this.name = name;
        this.departmentId = departmentId;
        this.skills = skills;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
    
    public String getSkills() {
        return skills;
    }
    
    public void setSkills(String skills) {
        this.skills = skills;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && 
               Objects.equals(name, employee.name) && 
               Objects.equals(departmentId, employee.departmentId) && 
               Objects.equals(skills, employee.skills);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, departmentId, skills);
    }
    
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", departmentId=" + departmentId +
                ", skills='" + skills + '\'' +
                '}';
    }
}
