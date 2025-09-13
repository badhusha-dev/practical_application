package com.lcnc.platform.controller;

import com.lcnc.platform.service.WorkflowExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow-execution")
public class WorkflowExecutionController {
    
    @Autowired
    private WorkflowExecutionService workflowExecutionService;
    
    @PostMapping("/start/{processDefinitionKey}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> startWorkflowInstance(
            @PathVariable String processDefinitionKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        try {
            Map<String, Object> workflowVariables = variables != null ? variables : new HashMap<>();
            String processInstanceId = workflowExecutionService.startWorkflowInstance(
                    processDefinitionKey, 
                    workflowVariables
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("processInstanceId", processInstanceId);
            response.put("status", "STARTED");
            response.put("message", "Workflow instance started successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to start workflow instance: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/complete-task/{taskId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        try {
            Map<String, Object> taskVariables = variables != null ? variables : new HashMap<>();
            workflowExecutionService.completeTask(taskId, taskVariables);
            
            Map<String, String> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("status", "COMPLETED");
            response.put("message", "Task completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to complete task: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/tasks/{processInstanceId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getActiveTasks(@PathVariable String processInstanceId) {
        try {
            List<org.camunda.bpm.engine.task.Task> tasks = 
                    workflowExecutionService.getActiveTasks(processInstanceId);
            
            List<Map<String, Object>> taskList = tasks.stream()
                    .map(task -> {
                        Map<String, Object> taskInfo = new HashMap<>();
                        taskInfo.put("id", task.getId());
                        taskInfo.put("name", task.getName());
                        taskInfo.put("assignee", task.getAssignee());
                        taskInfo.put("created", task.getCreateTime());
                        taskInfo.put("processInstanceId", task.getProcessInstanceId());
                        return taskInfo;
                    })
                    .toList();
            
            return ResponseEntity.ok(taskList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user-tasks/{assignee}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getUserTasks(@PathVariable String assignee) {
        try {
            List<org.camunda.bpm.engine.task.Task> tasks = 
                    workflowExecutionService.getUserTasks(assignee);
            
            List<Map<String, Object>> taskList = tasks.stream()
                    .map(task -> {
                        Map<String, Object> taskInfo = new HashMap<>();
                        taskInfo.put("id", task.getId());
                        taskInfo.put("name", task.getName());
                        taskInfo.put("assignee", task.getAssignee());
                        taskInfo.put("created", task.getCreateTime());
                        taskInfo.put("processInstanceId", task.getProcessInstanceId());
                        return taskInfo;
                    })
                    .toList();
            
            return ResponseEntity.ok(taskList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/state/{processInstanceId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getWorkflowState(@PathVariable String processInstanceId) {
        try {
            Map<String, Object> state = workflowExecutionService.getWorkflowState(processInstanceId);
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get workflow state: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/suspend/{processInstanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> suspendProcessInstance(@PathVariable String processInstanceId) {
        try {
            workflowExecutionService.suspendProcessInstance(processInstanceId);
            
            Map<String, String> response = new HashMap<>();
            response.put("processInstanceId", processInstanceId);
            response.put("status", "SUSPENDED");
            response.put("message", "Process instance suspended successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to suspend process instance: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/activate/{processInstanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> activateProcessInstance(@PathVariable String processInstanceId) {
        try {
            workflowExecutionService.activateProcessInstance(processInstanceId);
            
            Map<String, String> response = new HashMap<>();
            response.put("processInstanceId", processInstanceId);
            response.put("status", "RUNNING");
            response.put("message", "Process instance activated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to activate process instance: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/{processInstanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProcessInstance(
            @PathVariable String processInstanceId,
            @RequestParam(defaultValue = "Manual deletion") String reason) {
        
        try {
            workflowExecutionService.deleteProcessInstance(processInstanceId, reason);
            
            Map<String, String> response = new HashMap<>();
            response.put("processInstanceId", processInstanceId);
            response.put("status", "DELETED");
            response.put("message", "Process instance deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete process instance: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
