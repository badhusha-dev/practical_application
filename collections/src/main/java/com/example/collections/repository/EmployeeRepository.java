package com.example.collections.repository;

import com.example.collections.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByDepartmentId(Integer departmentId);
    
    @Query("SELECT DISTINCT e.skills FROM Employee e WHERE e.skills IS NOT NULL")
    List<String> findAllUniqueSkills();
}
