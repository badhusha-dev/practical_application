package com.example.filestorageapp.repository;

import com.example.filestorageapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    Page<User> findAllAdmins(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:search% OR u.email LIKE %:search%")
    Page<User> findByUsernameOrEmailContaining(@Param("search") String search, Pageable pageable);
}
