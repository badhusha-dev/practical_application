package com.lcnc.platform.controller;

import com.lcnc.platform.entity.WorkflowDefinition;
import com.lcnc.platform.service.WorkflowDefinitionService;
import com.lcnc.platform.service.WorkflowExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {
    
    @Autowired
    private WorkflowDefinitionService workflowDefinitionService;
    
    @Autowired
    private WorkflowExecutionService workflowExecutionService;
    
    @GetMapping
    public String workflowDashboard(Model model) {
        List<WorkflowDefinition> workflows = workflowDefinitionService.findAll();
        List<WorkflowDefinition> deployedWorkflows = workflowDefinitionService.findDeployedWorkflows();
        
        model.addAttribute("workflows", workflows);
        model.addAttribute("deployedWorkflows", deployedWorkflows);
        return "workflow/dashboard";
    }
    
    @GetMapping("/upload")
    public String uploadForm(Model model) {
        model.addAttribute("workflowDefinition", new WorkflowDefinition());
        return "workflow/upload";
    }
    
    @PostMapping("/upload")
    public String uploadWorkflow(@RequestParam("file") MultipartFile file,
                                @RequestParam("name") String name,
                                @RequestParam(value = "description", required = false) String description,
                                Model model) {
        try {
            String bpmnXml = new String(file.getBytes());
            WorkflowDefinition workflowDefinition = new WorkflowDefinition();
            workflowDefinition.setName(name);
            workflowDefinition.setBpmnXml(bpmnXml);
            workflowDefinition.setDescription(description);
            
            WorkflowDefinition savedWorkflow = workflowDefinitionService.save(workflowDefinition);
            model.addAttribute("success", "Workflow uploaded successfully!");
            model.addAttribute("workflowId", savedWorkflow.getId());
            
            return "workflow/upload-success";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to upload workflow: " + e.getMessage());
            return "workflow/upload";
        }
    }
    
    @PostMapping("/{id}/deploy")
    public String deployWorkflow(@PathVariable Long id, Model model) {
        try {
            String deploymentId = workflowDefinitionService.deployWorkflow(id);
            model.addAttribute("success", "Workflow deployed successfully!");
            model.addAttribute("deploymentId", deploymentId);
            return "redirect:/workflow";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to deploy workflow: " + e.getMessage());
            return "redirect:/workflow";
        }
    }
    
    @PostMapping("/{id}/undeploy")
    public String undeployWorkflow(@PathVariable Long id, Model model) {
        try {
            workflowDefinitionService.undeployWorkflow(id);
            model.addAttribute("success", "Workflow undeployed successfully!");
            return "redirect:/workflow";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to undeploy workflow: " + e.getMessage());
            return "redirect:/workflow";
        }
    }
    
    @GetMapping("/{id}/start")
    public String startWorkflowForm(@PathVariable Long id, Model model) {
        WorkflowDefinition workflow = workflowDefinitionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        model.addAttribute("workflow", workflow);
        return "workflow/start-instance";
    }
    
    @PostMapping("/{id}/start")
    public String startWorkflowInstance(@PathVariable Long id,
                                       @RequestParam Map<String, Object> variables,
                                       Model model) {
        try {
            WorkflowDefinition workflow = workflowDefinitionService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Workflow not found"));
            
            if (!workflow.getDeployed()) {
                throw new RuntimeException("Workflow must be deployed before starting an instance");
            }
            
            String processInstanceId = workflowExecutionService.startWorkflowInstance(
                    workflow.getProcessDefinitionKey(), 
                    variables
            );
            
            model.addAttribute("success", "Workflow instance started successfully!");
            model.addAttribute("processInstanceId", processInstanceId);
            return "workflow/instance-started";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to start workflow instance: " + e.getMessage());
            return "workflow/start-instance";
        }
    }
    
    @GetMapping("/instances")
    public String workflowInstances(Model model) {
        // This would typically fetch from a workflow instances service
        // For now, we'll show a placeholder
        return "workflow/instances";
    }
}
