package com.example.collections.repository;

import com.example.collections.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByPriorityOrderByPriorityDesc(Integer priority);
    
    @Query("SELECT t FROM Task t ORDER BY t.priority DESC")
    List<Task> findAllOrderByPriorityDesc();
}
