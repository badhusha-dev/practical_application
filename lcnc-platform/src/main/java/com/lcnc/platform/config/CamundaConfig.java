package com.lcnc.platform.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableProcessApplication
public class CamundaConfig {
    
    @Autowired
    private ProcessEngine processEngine;
    
    @Bean
    public RuntimeService runtimeService() {
        return processEngine.getRuntimeService();
    }
    
    @Bean
    public TaskService taskService() {
        return processEngine.getTaskService();
    }
    
    @Bean
    public RepositoryService repositoryService() {
        return processEngine.getRepositoryService();
    }
}
