package com.lcnc.platform.controller;

import com.lcnc.platform.entity.AppDefinition;
import com.lcnc.platform.service.AppDefinitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/apps")
public class AppDefinitionController {
    
    @Autowired
    private AppDefinitionService appDefinitionService;
    
    @GetMapping
    public ResponseEntity<List<AppDefinition>> getAllApps() {
        List<AppDefinition> apps = appDefinitionService.findAll();
        return ResponseEntity.ok(apps);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Page<AppDefinition>> getAllAppsPaginated(Pageable pageable) {
        Page<AppDefinition> apps = appDefinitionService.findAllPaginated(pageable);
        return ResponseEntity.ok(apps);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AppDefinition> getAppById(@PathVariable Long id) {
        Optional<AppDefinition> app = appDefinitionService.findById(id);
        return app.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<AppDefinition> getAppByName(@PathVariable String name) {
        Optional<AppDefinition> app = appDefinitionService.findByName(name);
        return app.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppDefinition> createApp(@Valid @RequestBody AppDefinition appDefinition) {
        AppDefinition createdApp = appDefinitionService.save(appDefinition);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdApp);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppDefinition> updateApp(@PathVariable Long id, 
                                                  @Valid @RequestBody AppDefinition appDefinition) {
        Optional<AppDefinition> existingApp = appDefinitionService.findById(id);
        if (existingApp.isPresent()) {
            appDefinition.setId(id);
            AppDefinition updatedApp = appDefinitionService.save(appDefinition);
            return ResponseEntity.ok(updatedApp);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteApp(@PathVariable Long id) {
        if (appDefinitionService.existsById(id)) {
            appDefinitionService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<AppDefinition>> getActiveApps() {
        List<AppDefinition> activeApps = appDefinitionService.findByActiveTrue();
        return ResponseEntity.ok(activeApps);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<AppDefinition>> searchApps(@RequestParam String name) {
        List<AppDefinition> apps = appDefinitionService.findByNameContaining(name);
        return ResponseEntity.ok(apps);
    }
}
