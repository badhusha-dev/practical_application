package com.lcnc.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_definitions")
public class WorkflowDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Workflow name is required")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @NotNull(message = "BPMN XML is required")
    @Column(name = "bpmn_xml", columnDefinition = "TEXT", nullable = false)
    private String bpmnXml;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "version")
    private String version = "1.0.0";
    
    @Column(name = "deployed")
    private Boolean deployed = false;
    
    @Column(name = "camunda_deployment_id")
    private String camundaDeploymentId;
    
    @Column(name = "process_definition_key")
    private String processDefinitionKey;
    
    @Column(name = "active")
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public WorkflowDefinition() {}
    
    public WorkflowDefinition(String name, String bpmnXml) {
        this.name = name;
        this.bpmnXml = bpmnXml;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBpmnXml() {
        return bpmnXml;
    }
    
    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Boolean getDeployed() {
        return deployed;
    }
    
    public void setDeployed(Boolean deployed) {
        this.deployed = deployed;
    }
    
    public String getCamundaDeploymentId() {
        return camundaDeploymentId;
    }
    
    public void setCamundaDeploymentId(String camundaDeploymentId) {
        this.camundaDeploymentId = camundaDeploymentId;
    }
    
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "WorkflowDefinition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", deployed=" + deployed +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
