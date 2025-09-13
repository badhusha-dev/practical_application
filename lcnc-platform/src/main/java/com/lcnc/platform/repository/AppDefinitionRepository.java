package com.lcnc.platform.repository;

import com.lcnc.platform.entity.AppDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppDefinitionRepository extends JpaRepository<AppDefinition, Long> {
    
    Optional<AppDefinition> findByName(String name);
    
    List<AppDefinition> findByActiveTrue();
    
    List<AppDefinition> findByActiveFalse();
    
    @Query("SELECT a FROM AppDefinition a WHERE a.name LIKE %:name%")
    List<AppDefinition> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT a FROM AppDefinition a WHERE a.version = :version")
    List<AppDefinition> findByVersion(@Param("version") String version);
    
    boolean existsByName(String name);
}
