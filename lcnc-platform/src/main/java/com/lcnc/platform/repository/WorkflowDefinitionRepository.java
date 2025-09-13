package com.lcnc.platform.repository;

import com.lcnc.platform.entity.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
    
    Optional<WorkflowDefinition> findByName(String name);
    
    List<WorkflowDefinition> findByActiveTrue();
    
    List<WorkflowDefinition> findByActiveFalse();
    
    List<WorkflowDefinition> findByDeployedTrue();
    
    List<WorkflowDefinition> findByDeployedFalse();
    
    @Query("SELECT w FROM WorkflowDefinition w WHERE w.name LIKE %:name%")
    List<WorkflowDefinition> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT w FROM WorkflowDefinition w WHERE w.version = :version")
    List<WorkflowDefinition> findByVersion(@Param("version") String version);
    
    Optional<WorkflowDefinition> findByCamundaDeploymentId(String camundaDeploymentId);
    
    Optional<WorkflowDefinition> findByProcessDefinitionKey(String processDefinitionKey);
    
    boolean existsByName(String name);
    
    @Query("SELECT w FROM WorkflowDefinition w WHERE w.deployed = true AND w.active = true")
    List<WorkflowDefinition> findDeployedActiveWorkflows();
}
