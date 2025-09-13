package com.lcnc.platform.service;

import com.lcnc.platform.entity.AppDefinition;
import com.lcnc.platform.repository.AppDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppDefinitionService {
    
    @Autowired
    private AppDefinitionRepository appDefinitionRepository;
    
    public List<AppDefinition> findAll() {
        return appDefinitionRepository.findAll();
    }
    
    public Page<AppDefinition> findAllPaginated(Pageable pageable) {
        return appDefinitionRepository.findAll(pageable);
    }
    
    public Optional<AppDefinition> findById(Long id) {
        return appDefinitionRepository.findById(id);
    }
    
    public Optional<AppDefinition> findByName(String name) {
        return appDefinitionRepository.findByName(name);
    }
    
    public AppDefinition save(AppDefinition appDefinition) {
        return appDefinitionRepository.save(appDefinition);
    }
    
    public void deleteById(Long id) {
        appDefinitionRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return appDefinitionRepository.existsById(id);
    }
    
    public List<AppDefinition> findByActiveTrue() {
        return appDefinitionRepository.findByActiveTrue();
    }
    
    public List<AppDefinition> findByActiveFalse() {
        return appDefinitionRepository.findByActiveFalse();
    }
    
    public List<AppDefinition> findByNameContaining(String name) {
        return appDefinitionRepository.findByNameContaining(name);
    }
    
    public List<AppDefinition> findByVersion(String version) {
        return appDefinitionRepository.findByVersion(version);
    }
    
    public boolean existsByName(String name) {
        return appDefinitionRepository.existsByName(name);
    }
}
