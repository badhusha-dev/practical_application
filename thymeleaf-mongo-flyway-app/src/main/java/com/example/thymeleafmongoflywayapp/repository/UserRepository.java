package com.example.thymeleafmongoflywayapp.repository;

import com.example.thymeleafmongoflywayapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<User> findByNameContainingIgnoreCase(String name);
    
    @Query("{'email': {$regex: ?0, $options: 'i'}}")
    List<User> findByEmailContainingIgnoreCase(String email);
    
    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'email': {$regex: ?0, $options: 'i'}}]}")
    Page<User> findByNameOrEmailContainingIgnoreCase(String searchTerm, Pageable pageable);
    
    @Query("{'age': {$gte: ?0, $lte: ?1}}")
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);
    
    @Query("{}")
    Page<User> findAllWithPagination(Pageable pageable);
}
