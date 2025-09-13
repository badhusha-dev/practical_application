package com.lcnc.platform.service;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WorkflowExecutionService {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public String startWorkflowInstance(String processDefinitionKey, Map<String, Object> variables) {
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                processDefinitionKey, 
                variables
            );
            
            // Cache workflow state in Redis
            String cacheKey = "workflow:instance:" + processInstance.getId();
            Map<String, Object> workflowState = new HashMap<>();
            workflowState.put("processInstanceId", processInstance.getId());
            workflowState.put("processDefinitionKey", processDefinitionKey);
            workflowState.put("variables", variables);
            workflowState.put("status", "RUNNING");
            workflowState.put("startTime", System.currentTimeMillis());
            
            redisTemplate.opsForHash().putAll(cacheKey, workflowState);
            redisTemplate.expire(cacheKey, 24, TimeUnit.HOURS);
            
            return processInstance.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start workflow instance: " + e.getMessage(), e);
        }
    }
    
    public void completeTask(String taskId, Map<String, Object> variables) {
        try {
            taskService.complete(taskId, variables);
            
            // Update workflow state in Redis
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task != null) {
                String cacheKey = "workflow:instance:" + task.getProcessInstanceId();
                Map<String, Object> workflowState = redisTemplate.opsForHash().entries(cacheKey);
                if (!workflowState.isEmpty()) {
                    workflowState.put("lastTaskCompleted", taskId);
                    workflowState.put("lastTaskCompletedTime", System.currentTimeMillis());
                    redisTemplate.opsForHash().putAll(cacheKey, workflowState);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete task: " + e.getMessage(), e);
        }
    }
    
    public List<Task> getActiveTasks(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();
    }
    
    public List<Task> getUserTasks(String assignee) {
        return taskService.createTaskQuery()
                .taskAssignee(assignee)
                .active()
                .list();
    }
    
    public Map<String, Object> getWorkflowState(String processInstanceId) {
        String cacheKey = "workflow:instance:" + processInstanceId;
        Map<Object, Object> cachedState = redisTemplate.opsForHash().entries(cacheKey);
        
        Map<String, Object> workflowState = new HashMap<>();
        for (Map.Entry<Object, Object> entry : cachedState.entrySet()) {
            workflowState.put(entry.getKey().toString(), entry.getValue());
        }
        
        // Add current tasks
        List<Task> activeTasks = getActiveTasks(processInstanceId);
        workflowState.put("activeTasks", activeTasks);
        
        return workflowState;
    }
    
    public void suspendProcessInstance(String processInstanceId) {
        try {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            
            // Update state in Redis
            String cacheKey = "workflow:instance:" + processInstanceId;
            redisTemplate.opsForHash().put(cacheKey, "status", "SUSPENDED");
            redisTemplate.opsForHash().put(cacheKey, "suspendedTime", System.currentTimeMillis());
        } catch (Exception e) {
            throw new RuntimeException("Failed to suspend process instance: " + e.getMessage(), e);
        }
    }
    
    public void activateProcessInstance(String processInstanceId) {
        try {
            runtimeService.activateProcessInstanceById(processInstanceId);
            
            // Update state in Redis
            String cacheKey = "workflow:instance:" + processInstanceId;
            redisTemplate.opsForHash().put(cacheKey, "status", "RUNNING");
            redisTemplate.opsForHash().put(cacheKey, "activatedTime", System.currentTimeMillis());
        } catch (Exception e) {
            throw new RuntimeException("Failed to activate process instance: " + e.getMessage(), e);
        }
    }
    
    public void deleteProcessInstance(String processInstanceId, String reason) {
        try {
            runtimeService.deleteProcessInstance(processInstanceId, reason);
            
            // Remove from Redis cache
            String cacheKey = "workflow:instance:" + processInstanceId;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete process instance: " + e.getMessage(), e);
        }
    }
}
