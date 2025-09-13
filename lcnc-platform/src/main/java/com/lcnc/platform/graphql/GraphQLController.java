package com.lcnc.platform.graphql;

import com.lcnc.platform.entity.AppDefinition;
import com.lcnc.platform.entity.WorkflowDefinition;
import com.lcnc.platform.service.AppDefinitionService;
import com.lcnc.platform.service.WorkflowDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class GraphQLController {
    
    @Autowired
    private AppDefinitionService appDefinitionService;
    
    @Autowired
    private WorkflowDefinitionService workflowDefinitionService;
    
    // App Queries
    @QueryMapping
    public List<AppDefinition> apps() {
        return appDefinitionService.findAll();
    }
    
    @QueryMapping
    public Optional<AppDefinition> app(@Argument Long id) {
        return appDefinitionService.findById(id);
    }
    
    @QueryMapping
    public Optional<AppDefinition> appByName(@Argument String name) {
        return appDefinitionService.findByName(name);
    }
    
    @QueryMapping
    public List<AppDefinition> activeApps() {
        return appDefinitionService.findByActiveTrue();
    }
    
    // Workflow Queries
    @QueryMapping
    public List<WorkflowDefinition> workflows() {
        return workflowDefinitionService.findAll();
    }
    
    @QueryMapping
    public Optional<WorkflowDefinition> workflow(@Argument Long id) {
        return workflowDefinitionService.findById(id);
    }
    
    @QueryMapping
    public Optional<WorkflowDefinition> workflowByName(@Argument String name) {
        return workflowDefinitionService.findByName(name);
    }
    
    @QueryMapping
    public List<WorkflowDefinition> deployedWorkflows() {
        return workflowDefinitionService.findDeployedWorkflows();
    }
    
    @QueryMapping
    public List<WorkflowDefinition> activeWorkflows() {
        return workflowDefinitionService.findByActiveTrue();
    }
    
    // App Mutations
    @MutationMapping
    public AppDefinition createApp(@Argument AppInput input) {
        AppDefinition app = new AppDefinition();
        app.setName(input.getName());
        app.setConfigJson(input.getConfigJson());
        app.setDescription(input.getDescription());
        app.setVersion(input.getVersion() != null ? input.getVersion() : "1.0.0");
        app.setActive(input.getActive() != null ? input.getActive() : true);
        return appDefinitionService.save(app);
    }
    
    @MutationMapping
    public AppDefinition updateApp(@Argument Long id, @Argument AppInput input) {
        Optional<AppDefinition> existingApp = appDefinitionService.findById(id);
        if (existingApp.isPresent()) {
            AppDefinition app = existingApp.get();
            app.setName(input.getName());
            app.setConfigJson(input.getConfigJson());
            app.setDescription(input.getDescription());
            if (input.getVersion() != null) app.setVersion(input.getVersion());
            if (input.getActive() != null) app.setActive(input.getActive());
            return appDefinitionService.save(app);
        }
        throw new RuntimeException("App not found with id: " + id);
    }
    
    @MutationMapping
    public Boolean deleteApp(@Argument Long id) {
        if (appDefinitionService.existsById(id)) {
            appDefinitionService.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Workflow Mutations
    @MutationMapping
    public WorkflowDefinition createWorkflow(@Argument WorkflowInput input) {
        WorkflowDefinition workflow = new WorkflowDefinition();
        workflow.setName(input.getName());
        workflow.setBpmnXml(input.getBpmnXml());
        workflow.setDescription(input.getDescription());
        workflow.setVersion(input.getVersion() != null ? input.getVersion() : "1.0.0");
        workflow.setActive(input.getActive() != null ? input.getActive() : true);
        return workflowDefinitionService.save(workflow);
    }
    
    @MutationMapping
    public WorkflowDefinition updateWorkflow(@Argument Long id, @Argument WorkflowInput input) {
        Optional<WorkflowDefinition> existingWorkflow = workflowDefinitionService.findById(id);
        if (existingWorkflow.isPresent()) {
            WorkflowDefinition workflow = existingWorkflow.get();
            workflow.setName(input.getName());
            workflow.setBpmnXml(input.getBpmnXml());
            workflow.setDescription(input.getDescription());
            if (input.getVersion() != null) workflow.setVersion(input.getVersion());
            if (input.getActive() != null) workflow.setActive(input.getActive());
            return workflowDefinitionService.save(workflow);
        }
        throw new RuntimeException("Workflow not found with id: " + id);
    }
    
    @MutationMapping
    public Boolean deleteWorkflow(@Argument Long id) {
        if (workflowDefinitionService.existsById(id)) {
            workflowDefinitionService.deleteById(id);
            return true;
        }
        return false;
    }
    
    @MutationMapping
    public String deployWorkflow(@Argument Long id) {
        return workflowDefinitionService.deployWorkflow(id);
    }
    
    @MutationMapping
    public String undeployWorkflow(@Argument Long id) {
        workflowDefinitionService.undeployWorkflow(id);
        return "Workflow undeployed successfully";
    }
    
    // Input DTOs
    public static class AppInput {
        private String name;
        private String configJson;
        private String description;
        private String version;
        private Boolean active;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getConfigJson() { return configJson; }
        public void setConfigJson(String configJson) { this.configJson = configJson; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
    
    public static class WorkflowInput {
        private String name;
        private String bpmnXml;
        private String description;
        private String version;
        private Boolean active;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getBpmnXml() { return bpmnXml; }
        public void setBpmnXml(String bpmnXml) { this.bpmnXml = bpmnXml; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
}
