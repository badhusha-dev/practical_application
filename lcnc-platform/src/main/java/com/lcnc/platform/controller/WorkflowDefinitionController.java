package com.lcnc.platform.controller;

import com.lcnc.platform.entity.WorkflowDefinition;
import com.lcnc.platform.service.WorkflowDefinitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowDefinitionController {
    
    @Autowired
    private WorkflowDefinitionService workflowDefinitionService;
    
    @GetMapping
    public ResponseEntity<List<WorkflowDefinition>> getAllWorkflows() {
        List<WorkflowDefinition> workflows = workflowDefinitionService.findAll();
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Page<WorkflowDefinition>> getAllWorkflowsPaginated(Pageable pageable) {
        Page<WorkflowDefinition> workflows = workflowDefinitionService.findAllPaginated(pageable);
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDefinition> getWorkflowById(@PathVariable Long id) {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.findById(id);
        return workflow.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<WorkflowDefinition> getWorkflowByName(@PathVariable String name) {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.findByName(name);
        return workflow.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDefinition> createWorkflow(@Valid @RequestBody WorkflowDefinition workflowDefinition) {
        WorkflowDefinition createdWorkflow = workflowDefinitionService.save(workflowDefinition);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkflow);
    }
    
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDefinition> uploadWorkflow(@RequestParam("file") MultipartFile file,
                                                           @RequestParam("name") String name,
                                                           @RequestParam(value = "description", required = false) String description) {
        try {
            String bpmnXml = new String(file.getBytes());
            WorkflowDefinition workflowDefinition = new WorkflowDefinition();
            workflowDefinition.setName(name);
            workflowDefinition.setBpmnXml(bpmnXml);
            workflowDefinition.setDescription(description);
            
            WorkflowDefinition createdWorkflow = workflowDefinitionService.save(workflowDefinition);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkflow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDefinition> updateWorkflow(@PathVariable Long id, 
                                                            @Valid @RequestBody WorkflowDefinition workflowDefinition) {
        Optional<WorkflowDefinition> existingWorkflow = workflowDefinitionService.findById(id);
        if (existingWorkflow.isPresent()) {
            workflowDefinition.setId(id);
            WorkflowDefinition updatedWorkflow = workflowDefinitionService.save(workflowDefinition);
            return ResponseEntity.ok(updatedWorkflow);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        if (workflowDefinitionService.existsById(id)) {
            workflowDefinitionService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/deploy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deployWorkflow(@PathVariable Long id) {
        try {
            String deploymentId = workflowDefinitionService.deployWorkflow(id);
            return ResponseEntity.ok("Workflow deployed successfully. Deployment ID: " + deploymentId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to deploy workflow: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/undeploy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> undeployWorkflow(@PathVariable Long id) {
        try {
            workflowDefinitionService.undeployWorkflow(id);
            return ResponseEntity.ok("Workflow undeployed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to undeploy workflow: " + e.getMessage());
        }
    }
    
    @GetMapping("/deployed")
    public ResponseEntity<List<WorkflowDefinition>> getDeployedWorkflows() {
        List<WorkflowDefinition> deployedWorkflows = workflowDefinitionService.findDeployedWorkflows();
        return ResponseEntity.ok(deployedWorkflows);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<WorkflowDefinition>> getActiveWorkflows() {
        List<WorkflowDefinition> activeWorkflows = workflowDefinitionService.findByActiveTrue();
        return ResponseEntity.ok(activeWorkflows);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<WorkflowDefinition>> searchWorkflows(@RequestParam String name) {
        List<WorkflowDefinition> workflows = workflowDefinitionService.findByNameContaining(name);
        return ResponseEntity.ok(workflows);
    }
}
