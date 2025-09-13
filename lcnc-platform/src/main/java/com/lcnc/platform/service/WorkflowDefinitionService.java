package com.lcnc.platform.service;

import com.lcnc.platform.entity.WorkflowDefinition;
import com.lcnc.platform.repository.WorkflowDefinitionRepository;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkflowDefinitionService {
    
    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;
    
    @Autowired
    private ProcessEngine processEngine;
    
    public List<WorkflowDefinition> findAll() {
        return workflowDefinitionRepository.findAll();
    }
    
    public Page<WorkflowDefinition> findAllPaginated(Pageable pageable) {
        return workflowDefinitionRepository.findAll(pageable);
    }
    
    public Optional<WorkflowDefinition> findById(Long id) {
        return workflowDefinitionRepository.findById(id);
    }
    
    public Optional<WorkflowDefinition> findByName(String name) {
        return workflowDefinitionRepository.findByName(name);
    }
    
    public WorkflowDefinition save(WorkflowDefinition workflowDefinition) {
        return workflowDefinitionRepository.save(workflowDefinition);
    }
    
    public void deleteById(Long id) {
        workflowDefinitionRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return workflowDefinitionRepository.existsById(id);
    }
    
    public List<WorkflowDefinition> findByActiveTrue() {
        return workflowDefinitionRepository.findByActiveTrue();
    }
    
    public List<WorkflowDefinition> findByActiveFalse() {
        return workflowDefinitionRepository.findByActiveFalse();
    }
    
    public List<WorkflowDefinition> findDeployedWorkflows() {
        return workflowDefinitionRepository.findDeployedActiveWorkflows();
    }
    
    public List<WorkflowDefinition> findByNameContaining(String name) {
        return workflowDefinitionRepository.findByNameContaining(name);
    }
    
    public List<WorkflowDefinition> findByVersion(String version) {
        return workflowDefinitionRepository.findByVersion(version);
    }
    
    public boolean existsByName(String name) {
        return workflowDefinitionRepository.existsByName(name);
    }
    
    public String deployWorkflow(Long id) {
        Optional<WorkflowDefinition> workflowOpt = findById(id);
        if (workflowOpt.isEmpty()) {
            throw new RuntimeException("Workflow not found with id: " + id);
        }
        
        WorkflowDefinition workflow = workflowOpt.get();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        
        try {
            Deployment deployment = repositoryService.createDeployment()
                    .name(workflow.getName())
                    .addString(workflow.getName() + ".bpmn", workflow.getBpmnXml())
                    .deploy();
            
            // Update workflow with deployment info
            ProcessDefinition processDefinition = repositoryService
                    .createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();
            
            workflow.setDeployed(true);
            workflow.setCamundaDeploymentId(deployment.getId());
            workflow.setProcessDefinitionKey(processDefinition.getKey());
            save(workflow);
            
            return deployment.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to deploy workflow: " + e.getMessage(), e);
        }
    }
    
    public void undeployWorkflow(Long id) {
        Optional<WorkflowDefinition> workflowOpt = findById(id);
        if (workflowOpt.isEmpty()) {
            throw new RuntimeException("Workflow not found with id: " + id);
        }
        
        WorkflowDefinition workflow = workflowOpt.get();
        if (!workflow.getDeployed() || workflow.getCamundaDeploymentId() == null) {
            throw new RuntimeException("Workflow is not deployed");
        }
        
        RepositoryService repositoryService = processEngine.getRepositoryService();
        
        try {
            repositoryService.deleteDeployment(workflow.getCamundaDeploymentId(), true);
            
            // Update workflow
            workflow.setDeployed(false);
            workflow.setCamundaDeploymentId(null);
            workflow.setProcessDefinitionKey(null);
            save(workflow);
        } catch (Exception e) {
            throw new RuntimeException("Failed to undeploy workflow: " + e.getMessage(), e);
        }
    }
}
