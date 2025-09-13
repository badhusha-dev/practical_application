package com.example.thymeleafmongoflywayapp.migration;

import com.example.thymeleafmongoflywayapp.entity.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.time.LocalDateTime;

@ChangeUnit(id = "create-users-collection", order = "001", author = "system")
public class CreateUsersCollectionMigration {
    
    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        // Create users collection if it doesn't exist
        if (!mongoTemplate.collectionExists("users")) {
            mongoTemplate.createCollection("users");
        }
        
        // Create indexes
        IndexOperations indexOps = mongoTemplate.indexOps("users");
        
        // Create unique index on email
        indexOps.ensureIndex(new Index().on("email", org.springframework.data.domain.Sort.Direction.ASC).unique());
        
        // Create index on name for better search performance
        indexOps.ensureIndex(new Index().on("name", org.springframework.data.domain.Sort.Direction.ASC));
        
        // Create index on age for filtering
        indexOps.ensureIndex(new Index().on("age", org.springframework.data.domain.Sort.Direction.ASC));
        
        // Create compound index on name and email for search
        indexOps.ensureIndex(new Index().on("name", org.springframework.data.domain.Sort.Direction.ASC)
                .on("email", org.springframework.data.domain.Sort.Direction.ASC));
        
        // Create index on created_at for sorting
        indexOps.ensureIndex(new Index().on("created_at", org.springframework.data.domain.Sort.Direction.DESC));
    }
    
    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        // Drop the users collection
        if (mongoTemplate.collectionExists("users")) {
            mongoTemplate.dropCollection("users");
        }
    }
}
